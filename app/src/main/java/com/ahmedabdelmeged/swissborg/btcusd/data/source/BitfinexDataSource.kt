package com.ahmedabdelmeged.swissborg.btcusd.data.source

import androidx.annotation.VisibleForTesting
import com.ahmedabdelmeged.swissborg.btcusd.api.BitfinexApiConstants
import com.ahmedabdelmeged.swissborg.btcusd.api.BitfinexService
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookModel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookLevel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.action.OrderBooksSubscribeAction
import com.ahmedabdelmeged.swissborg.btcusd.data.model.action.TickerSubscribeAction
import com.ahmedabdelmeged.swissborg.btcusd.util.SchedulerProvider
import com.ahmedabdelmeged.swissborg.btcusd.testing.OpenForTesting
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * Data source to convert [BitfinexService] responses to something the data layer of the
 * app can understand an deal with. This class is created because the issue with Bitfinex API
 * responses are not designed to work well with modern serialization libraries like Gson so we parse
 * the responses manually and depending on the response type and body we will take the right action for it.
 */
@OpenForTesting
class BitfinexDataSource @Inject constructor(private val service: BitfinexService,
                                             private val schedulerProvider: SchedulerProvider) {

    private val gson = Gson()

    /**
     * Substations actions for the WebSocket channels
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tickerSubscribeAction = TickerSubscribeAction()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val orderBooksSubscribeAction = OrderBooksSubscribeAction()

    /**
     * PublishSubject to track the updates of the ticker and order book.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tickerPublishSubject: PublishSubject<List<Double>> = PublishSubject.create()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val orderBookPublishSubject: PublishSubject<OrderBookModel> = PublishSubject.create()

    /**
     * The current order book that we will update as we get updates from the order book channel.
     */
    private var orderBook = OrderBookModel()

    /**
     * The current channels we will subscribe to. We are holding the channel id here so when a new
     * data comes from the WebSocket we can determine which [PublishSubject] we will send the update to.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val channels = mutableMapOf<String, Int>().apply {
        put(tickerSubscribeAction.channel, -1)
        put(orderBooksSubscribeAction.channel, -1)
    }

    /**
     * Request a subscription to Bitfinex WebSocket channels so we get updates for the ticker and order book.
     */
    fun subscribeToBitfinexChannels(): Disposable {
        return service.observeWebSocketEvents()
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    // If connection is open subscribe to the channels.
                    if (it is WebSocket.Event.OnConnectionOpened<*>) {
                        service.subscribeToTicker(tickerSubscribeAction)
                        service.subscribeToOrderBooks(orderBooksSubscribeAction)
                    }

                    if (it is WebSocket.Event.OnMessageReceived) {
                        handleMessage(it.message)
                    }
                }, { error ->
                    Timber.e("Error while observing socket ${error.cause}")
                })
    }

    /**
     * Check if the response it just a heart beat update to keep the socket open then ignore it.
     * that will happen when there is no activity in the channel for 5 second.
     * Docs: https://docs.bitfinex.com/v2/docs/ws-general#section-heartbeating
     */
    private fun isHeartBeatingUpdate(element: JsonElement): Boolean {
        return element.isJsonPrimitive && element.asJsonPrimitive.isString && element.asString == "hb"
    }

    /**
     * Handle the response message from the WebSocket. It will return 3 different types of responses.
     *
     * 1- Event response handled by [handleEventMessages]
     * 2- Heart Beat response handled by [isHeartBeatingUpdate]
     * 3- Channel update response to update the values in the channels we are subscribed to.
     * Handled by [handleChannel]
     */
    private fun handleMessage(message: Message) {
        if (message is Message.Text) {
            try {
                val json = gson.fromJson<JsonElement>(message.value, JsonElement::class.java)

                //Check if it's heart beat update or normal update
                if (json.isJsonArray) {
                    val array = json.asJsonArray
                    val type = array.get(1)
                    if (isHeartBeatingUpdate(type)) {
                        return
                    } else {
                        handleChannel(array)
                    }
                }

                if (json.isJsonObject) {
                    handleEventMessages(json)
                }
            } catch (e: Exception) {
                Timber.e("Failed to parse json object: $e")
            }
        }
    }

    /**
     * Handle the event responses. they are usually the first response when we subscribe to a channel.
     */
    private fun handleEventMessages(json: JsonElement) {
        val response = json.asJsonObject
        val event = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_EVENT).asString
        if (event != null) {
            when (event) {
                BitfinexApiConstants.EVENT_SUBSCRIBED -> handleSubscribedEvent(response)
                BitfinexApiConstants.EVENT_UNSUBSCRIBED -> handleUnSubscribedEvent(response)
                BitfinexApiConstants.EVENT_ERROR -> handleErrorEvent(response)
            }
        }
    }

    /**
     * We now subscribed to a WebSocket channel check it's id and add it to [channels] so we can send
     * the correct updates to the [PublishSubject].
     */
    private fun handleSubscribedEvent(response: JsonObject) {
        val channel = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_CHANNEL).asString
        val chanId = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_CHANNEL_ID).asInt
        if (channels.containsKey(channel)) {
            channels[channel] = chanId
        }
    }

    /**
     * We now unsubscribed from a WebSocket channel check it's id and remove it from [channels]
     * so we can send the correct updates to the [PublishSubject].
     */
    private fun handleUnSubscribedEvent(response: JsonObject) {
        val channel = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_CHANNEL).asString
        val chanId = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_CHANNEL_ID).asInt
        if (channels.containsValue(chanId)) {
            channels[channel] = -1
        }
    }

    /**
     * Currently we don't handle WebSocket API error. Just log the error for now and it will
     * be handled in the production application.
     */
    private fun handleErrorEvent(response: JsonObject) {
        val errorMessage = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_MESSAGE).asString
        val errorCode = response.getAsJsonPrimitive(BitfinexApiConstants.FIELD_CODE).asInt
        Timber.e("Failed to subscribe. Code: $errorCode. Message: $errorMessage")
    }

    /**
     * Send the updates to the correct channel ticker ot order book.
     */
    private fun handleChannel(array: JsonArray) {
        if (array.size() > 0) {
            val channel = array[0].asInt
            if (channels[tickerSubscribeAction.channel] == channel) {
                handleTickerChannel(array)
            }

            if (channels[orderBooksSubscribeAction.channel] == channel) {
                handleOrderBookChannel(array)
            }
        }
    }

    /**
     * Send the ticker new values to the [tickerPublishSubject] after removing the channel id
     * from the array because the data layer doesn't care about it.
     */
    private fun handleTickerChannel(array: JsonArray) {
        val list = array.map { it.asDouble }.toMutableList()
                .apply { removeAt(0) }.toList()
        tickerPublishSubject.onNext(list)
    }

    /**
     * Send the order book new values to the [orderBooksSubscribeAction] after removing the channel id
     * from the array because the data layer doesn't care about it. Here we will check if it's
     * the initial order book or update to the current one.
     */
    private fun handleOrderBookChannel(array: JsonArray) {
        //Create Order Book
        if (array.get(1).isJsonArray) {
            val orderBookLevels = mutableListOf<OrderBookLevel>()

            array.get(1).asJsonArray.map { it.asJsonArray }.forEach { jsonArray ->
                orderBookLevels.add(OrderBookLevel.fromList(jsonArray.map { it.asDouble }))
            }

            orderBook = OrderBookModel.createFromBookOrderLevels(orderBookLevels)
            orderBookPublishSubject.onNext(orderBook)
        } else {
            //Update Order Book
            val list = array.map { it.asDouble }.toMutableList()
                    .apply { removeAt(0) }.toList()

            val level = OrderBookLevel.fromList(list)

            orderBook = orderBook.updateOrderBookLevel(level)
            orderBookPublishSubject.onNext(orderBook)
        }
    }

    fun tickerObservable(): Observable<List<Double>> {
        return tickerPublishSubject
    }

    fun orderBookObservable(): Observable<OrderBookModel> {
        return orderBookPublishSubject
    }

    @VisibleForTesting
    fun setChannelsId(ticker: Int, orderBook: Int) {
        channels[tickerSubscribeAction.channel] = ticker
        channels[orderBooksSubscribeAction.channel] = orderBook
    }

    @VisibleForTesting
    fun setOrderBookModel(orderBookModel: OrderBookModel) {
        this.orderBook = orderBookModel
    }

}
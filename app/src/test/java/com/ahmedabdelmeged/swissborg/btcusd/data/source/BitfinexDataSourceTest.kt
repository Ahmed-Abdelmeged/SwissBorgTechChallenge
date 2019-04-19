package com.ahmedabdelmeged.swissborg.btcusd.data.source

import com.ahmedabdelmeged.swissborg.btcusd.api.BitfinexService
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookLevel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookModel
import com.ahmedabdelmeged.swissborg.btcusd.data.util.JsonFileHelper
import com.ahmedabdelmeged.swissborg.btcusd.util.SchedulerProvider
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BitfinexDataSourceTest {

    private val service = mock<BitfinexService>()

    private val schedulerProvider = mock<SchedulerProvider>()

    private val source = BitfinexDataSource(service, schedulerProvider)

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
    }

    @Test
    fun subscribeToChannelsWhenWebSocketIsConnected() {
        whenever(service.observeWebSocketEvents())
                .thenReturn(Flowable.just(WebSocket.Event.OnConnectionOpened(Any())))

        source.subscribeToBitfinexChannels()

        verify(service, times(1)).subscribeToTicker(any())
        verify(service, times(1)).subscribeToOrderBooks(any())
    }

    @Test
    fun isHeartBeatingUpdate() {
        val response = JsonFileHelper.readJsonFile("socket_heart_beat.json")

        val tickerTestObserver = source.tickerPublishSubject.test()
        val orderBookTestObserver = source.orderBookPublishSubject.test()

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        //Make sure to channel updates to publish subjects
        tickerTestObserver.assertNoValues()
        orderBookTestObserver.assertNoValues()

        //Make sure no channel ids changes
        assertEquals(source.channels[source.tickerSubscribeAction.channel], -1)
        assertEquals(source.channels[source.orderBooksSubscribeAction.channel], -1)
    }

    @Test
    fun handleEventMessages_subscribedEvent_toTickerChannel() {
        val response = JsonFileHelper.readJsonFile("ticker_subscribed_event.json")

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        assertEquals(source.channels[source.tickerSubscribeAction.channel], 20)
        assertEquals(source.channels[source.orderBooksSubscribeAction.channel], -1)
    }

    @Test
    fun handleEventMessages_subscribedEvent_toOrderBookChannel() {
        val response = JsonFileHelper.readJsonFile("book_subscribed_event.json")

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        assertEquals(source.channels[source.tickerSubscribeAction.channel], -1)
        assertEquals(source.channels[source.orderBooksSubscribeAction.channel], 20)
    }

    @Test
    fun handleEventMessages_unsubscribedEvent_toTickerChannel() {
        //Initial state
        source.setChannelsId(20, 10)

        val response = JsonFileHelper.readJsonFile("ticker_unsubscribed_event.json")

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        assertEquals(source.channels[source.tickerSubscribeAction.channel], -1)
        assertEquals(source.channels[source.orderBooksSubscribeAction.channel], 10)
    }

    @Test
    fun tickerUpdateTriggerTheRightPublishSubjectChannel() {
        source.setChannelsId(20, 10)
        val response = JsonFileHelper.readJsonFile("ticker_update.json")

        val tickerTestObserver = source.tickerPublishSubject.test()
        val orderBookTestObserver = source.orderBookPublishSubject.test()

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        val result = listOf(236.62, 9.0029, 236.88, 7.1138, -1.02, 0.0, 236.52, 5191.36754297, 250.01, 220.05)

        tickerTestObserver.assertValue(result)
        orderBookTestObserver.assertNoValues()
    }

    @Test
    fun orderBookUpdateTriggerTheRightPublishSubjectChannel() {
        source.setChannelsId(20, 10)
        val response = JsonFileHelper.readJsonFile("book_initial.json")

        val tickerTestObserver = source.tickerPublishSubject.test()
        val orderBookTestObserver = source.orderBookPublishSubject.test()

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        val bids = hashMapOf<Double, OrderBookLevel>().apply {
            put(5294.0, OrderBookLevel(5294.0, 5, 6.09119492))
            put(5293.5, OrderBookLevel(5293.5, 1, 1.17781604))
        }

        val asks = hashMapOf<Double, OrderBookLevel>().apply {
            put(5299.0, OrderBookLevel(5299.0, 3, -0.6))
            put(5299.2, OrderBookLevel(5299.2, 1, -0.148))
        }

        val result = OrderBookModel(asks, bids)

        tickerTestObserver.assertNoValues()
        orderBookTestObserver.assertValue(result)
    }

    @Test
    fun orderBookUpdate_sendRightUpdatedOrderBookModel() {
        source.setChannelsId(20, 10)
        source.setOrderBookModel(generateOrderBookModel())

        val response = JsonFileHelper.readJsonFile("book_update.json")

        val orderBookTestObserver = source.orderBookPublishSubject.test()

        stubWebSocketResponse(response)
        source.subscribeToBitfinexChannels()

        val bids = hashMapOf<Double, OrderBookLevel>().apply {
            put(5293.5, OrderBookLevel(5293.5, 1, 1.17781604))
        }

        val asks = hashMapOf<Double, OrderBookLevel>().apply {
            put(5299.0, OrderBookLevel(5299.0, 3, -0.6))
            put(5299.2, OrderBookLevel(5299.2, 1, -0.148))
        }

        val result = OrderBookModel(asks, bids)

        orderBookTestObserver.assertValue(result)
    }


    private fun stubWebSocketResponse(response: String) {
        whenever(service.observeWebSocketEvents())
                .thenReturn(Flowable.just(WebSocket.Event.OnMessageReceived(Message.Text(value = response))))
    }

    private fun generateOrderBookModel(): OrderBookModel {
        val bids = hashMapOf<Double, OrderBookLevel>().apply {
            put(5294.0, OrderBookLevel(5294.0, 5, 6.09119492))
            put(5293.5, OrderBookLevel(5293.5, 1, 1.17781604))
        }

        val asks = hashMapOf<Double, OrderBookLevel>().apply {
            put(5299.0, OrderBookLevel(5299.0, 3, -0.6))
            put(5299.2, OrderBookLevel(5299.2, 1, -0.148))
        }

        return OrderBookModel(asks, bids)
    }

}
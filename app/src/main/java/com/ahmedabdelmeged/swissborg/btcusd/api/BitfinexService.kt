package com.ahmedabdelmeged.swissborg.btcusd.api

import com.ahmedabdelmeged.swissborg.btcusd.data.model.action.OrderBooksSubscribeAction
import com.ahmedabdelmeged.swissborg.btcusd.data.model.action.TickerSubscribeAction
import com.ahmedabdelmeged.swissborg.btcusd.data.source.BitfinexDataSource
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable
import com.tinder.scarlet.Scarlet

/**
 * [Scarlet] service to interact with Bitfinex WebSockets API.
 */
interface BitfinexService {

    /**
     * Send a subscription action to the ticker channel. You can configure
     * the subscription properties in [TickerSubscribeAction]
     */
    @Send
    fun subscribeToTicker(actionTicker: TickerSubscribeAction)

    /**
     * Send a subscription action to the order book channel. You can configure
     * the subscription properties in [OrderBooksSubscribeAction]
     */
    @Send
    fun subscribeToOrderBooks(actionOrderBooks: OrderBooksSubscribeAction)

    /**
     * Receive the WebSocket updates Status. That includes the connection status of
     * the WebSocket and the responses from Bitfinex WebSocket all the response
     * are handled in [BitfinexDataSource].
     */
    @Receive
    fun observeWebSocketEvents(): Flowable<WebSocket.Event>

}
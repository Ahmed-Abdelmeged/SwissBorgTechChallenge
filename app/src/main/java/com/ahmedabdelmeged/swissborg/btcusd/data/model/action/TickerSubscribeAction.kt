package com.ahmedabdelmeged.swissborg.btcusd.data.model.action

import com.google.gson.annotations.SerializedName

/**
 * Data model that represent the POST json needed to send to the Bitfinex API
 * inorder to subscribe to the ticker WebSocket channel.
 * Docs: https://docs.bitfinex.com/reference#ws-public-ticker
 */
data class TickerSubscribeAction(
        @SerializedName("event") val event: String = "subscribe",

        @SerializedName("channel") val channel: String = "ticker",

        @SerializedName("pair") val pair: String = "BTCUSD"
)
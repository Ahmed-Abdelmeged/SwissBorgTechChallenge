package com.ahmedabdelmeged.swissborg.btcusd.data.model.action

import com.google.gson.annotations.SerializedName

/**
 * Data model that represent the POST json needed to send to the Bitfinex API
 * inorder to subscribe to the book order WebSocket channel.
 * Docs: https://docs.bitfinex.com/reference#ws-public-order-books
 */
data class OrderBooksSubscribeAction(
        @SerializedName("event") val event: String = "subscribe",

        @SerializedName("channel") val channel: String = "book",

        @SerializedName("pair") val pair: String = "BTCUSD",

        @SerializedName("prec") val prec: String = "P0",

        //Send updates every 2 seconds.
        @SerializedName("freq") val freq: String = "F1"
)
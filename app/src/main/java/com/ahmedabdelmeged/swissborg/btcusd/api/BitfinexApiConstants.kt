package com.ahmedabdelmeged.swissborg.btcusd.api

object BitfinexApiConstants {

    const val BITFINEX_WEB_SOCKET_BASE_URL = "wss://api-pub.bitfinex.com/ws/1"

    //Events
    const val EVENT_SUBSCRIBED = "subscribed"
    const val EVENT_UNSUBSCRIBED = "unsubscribed"
    const val EVENT_ERROR = "error"

    //Fields
    const val FIELD_EVENT = "event"
    const val FIELD_CHANNEL = "channel"
    const val FIELD_CHANNEL_ID = "chanId"
    const val FIELD_CODE = "code"
    const val FIELD_MESSAGE = "msg"

}
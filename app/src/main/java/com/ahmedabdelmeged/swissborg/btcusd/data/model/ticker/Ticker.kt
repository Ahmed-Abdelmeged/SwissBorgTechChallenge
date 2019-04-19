package com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker

import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.TickerMapper

/**
 * UI representation of the ticker response from the Bitfinex API mapped by [TickerMapper].
 */
data class Ticker(
        val change: Double,
        val lastPrice: Double,
        val volume: Double,
        val high: Double,
        val low: Double
)
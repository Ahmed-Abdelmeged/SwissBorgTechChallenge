package com.ahmedabdelmeged.swissborg.btcusd.data.mapper

import com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker.Ticker
import com.ahmedabdelmeged.swissborg.btcusd.testing.OpenForTesting
import io.reactivex.functions.Function
import javax.inject.Inject

/**
 * Mapper to map from [List] of ticker double values from the api to a [Ticker] UI model.
 * You can find all the representation of the array values at Bitfinex API docs.
 * https://docs.bitfinex.com/reference#ws-public-ticker
 */
@OpenForTesting
class TickerMapper @Inject constructor() : Function<List<Double>, Ticker> {

    override fun apply(t: List<Double>): Ticker {
        return Ticker(
                change = t[5],
                lastPrice = t[6],
                volume = t[7],
                high = t[8],
                low = t[9]
        )
    }

}
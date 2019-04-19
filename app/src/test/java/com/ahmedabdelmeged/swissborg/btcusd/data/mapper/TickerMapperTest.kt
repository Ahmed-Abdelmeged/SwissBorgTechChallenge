package com.ahmedabdelmeged.swissborg.btcusd.data.mapper

import com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker.Ticker
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TickerMapperTest {

    private val mapper = TickerMapper()

    @Test
    fun mapTickerArrayToTicker() {
        //Test data
        val array = listOf(
                236.62, 9.0029, 236.88, 7.1138, -1.02, 0.0, 236.52, 5191.36754297, 250.01, 220.05
        )

        val actual = mapper.apply(array)

        val expected = Ticker(
                change = 0.0,
                lastPrice = 236.52,
                volume = 5191.36754297,
                high = 250.01,
                low = 220.05)

        assertEquals(expected, actual)
    }

}
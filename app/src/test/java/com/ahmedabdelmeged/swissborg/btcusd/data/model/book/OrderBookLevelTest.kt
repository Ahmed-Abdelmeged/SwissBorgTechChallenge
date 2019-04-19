package com.ahmedabdelmeged.swissborg.btcusd.data.model.book

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrderBookLevelTest {

    @Test
    fun fromListToOrderBookLevel() {
        val response = listOf(5000.0, 1.0, 0.251)

        val actual = OrderBookLevel.fromList(response)
        val expected = OrderBookLevel(price = 5000.0, count = 1, amount = 0.251)

        assertEquals(expected, actual)
    }

}
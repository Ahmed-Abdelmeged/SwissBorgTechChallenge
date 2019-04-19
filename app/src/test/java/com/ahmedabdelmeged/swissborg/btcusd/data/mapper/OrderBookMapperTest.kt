package com.ahmedabdelmeged.swissborg.btcusd.data.mapper

import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBook
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookLevel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrderBookMapperTest {

    private val mapper = OrderBookMapper()

    @Test
    fun mapOrderBookModelToOrderBook() {
        //Test data
        val bids = hashMapOf<Double, OrderBookLevel>().apply {
            put(5000.0, OrderBookLevel(5000.0, 0, 0.5))
            put(5100.0, OrderBookLevel(5100.0, 0, 0.4))
        }

        val asks = hashMapOf<Double, OrderBookLevel>().apply {
            put(4000.0, OrderBookLevel(4000.0, 0, -0.5))
            put(4100.0, OrderBookLevel(4100.0, 0, -0.4))
        }

        val orderBookModel = OrderBookModel(bids = bids, asks = asks)

        val actual = mapper.apply(orderBookModel)

        val expected = OrderBook(
                bidsAmount = "0.40\n0.50\n",
                bidsPrice = "5100.00\n5000.00\n",
                asksAmount = "0.40\n0.50\n",
                asksPrice = "4100.00\n4000.00\n")

        assertEquals(expected, actual)
    }

}
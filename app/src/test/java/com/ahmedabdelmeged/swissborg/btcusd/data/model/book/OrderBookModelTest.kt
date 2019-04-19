package com.ahmedabdelmeged.swissborg.btcusd.data.model.book

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OrderBookModelTest {

    @Test
    fun createBookOrderModelFromBookOrderLevels() {
        val response = generateOrderBookLevels()
        val orderBookModel = OrderBookModel.createFromBookOrderLevels(response)

        assertEquals(orderBookModel.bids.size, 2)
        assertEquals(orderBookModel.asks.size, 2)

        assertArrayEquals(orderBookModel.bids.keys.toTypedArray(), arrayOf(5000.0, 5300.0))
        assertArrayEquals(orderBookModel.asks.keys.toTypedArray(), arrayOf(5100.0, 5200.0))
    }

    @Test
    fun updateBookOrderModel_addBookOrderLevel() {
        val update = OrderBookLevel(price = 5500.0, count = 2, amount = 7.25)

        var orderBookModel = OrderBookModel.createFromBookOrderLevels(generateOrderBookLevels())
        orderBookModel = orderBookModel.updateOrderBookLevel(update)

        assertEquals(orderBookModel.bids.size, 3)
        assertEquals(orderBookModel.asks.size, 2)

        assertArrayEquals(orderBookModel.bids.keys.toTypedArray(), arrayOf(5000.0, 5300.0, 5500.0))
        assertArrayEquals(orderBookModel.asks.keys.toTypedArray(), arrayOf(5100.0, 5200.0))
    }

    @Test
    fun updateBookOrderModel_removeBookOrderLevel() {
        val update = OrderBookLevel(price = 5000.0, count = 0, amount = 0.25)

        var orderBookModel = OrderBookModel.createFromBookOrderLevels(generateOrderBookLevels())
        orderBookModel = orderBookModel.updateOrderBookLevel(update)

        assertEquals(orderBookModel.bids.size, 1)
        assertEquals(orderBookModel.asks.size, 2)

        assertArrayEquals(orderBookModel.bids.keys.toTypedArray(), arrayOf(5300.0))
        assertArrayEquals(orderBookModel.asks.keys.toTypedArray(), arrayOf(5100.0, 5200.0))
    }

    companion object {
        fun generateOrderBookLevels(): List<OrderBookLevel> {
            return listOf(
                OrderBookLevel(price = 5000.0, count = 1, amount = 0.25),
                OrderBookLevel(price = 5100.0, count = 2, amount = -1.25),
                OrderBookLevel(price = 5200.0, count = 3, amount = -2.25),
                OrderBookLevel(price = 5300.0, count = 4, amount = 3.25),
                OrderBookLevel(price = 5400.0, count = 0, amount = 3.25)
            )
        }
    }

}
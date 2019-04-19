package com.ahmedabdelmeged.swissborg.btcusd.data.mapper

import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBook
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookLevel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookModel
import com.ahmedabdelmeged.swissborg.btcusd.testing.OpenForTesting
import io.reactivex.functions.Function
import javax.inject.Inject
import kotlin.math.absoluteValue

/**
 * Mapper to map from [OrderBookModel] data model to [OrderBook] UI model. Since the UI
 * only need the asks and bids lists and doesn't care about the other functionality.
 * It will return a sorted list(in a form of a string) of the bids and asks in a descending order.
 */
@OpenForTesting
class OrderBookMapper @Inject constructor() : Function<OrderBookModel, OrderBook> {

    override fun apply(book: OrderBookModel): OrderBook {
        val bids = fromOrderBookMapToString(book.bids)
        val asks = fromOrderBookMapToString(book.asks)

        return OrderBook(
                bidsAmount = bids.amount,
                bidsPrice = bids.price,
                asksAmount = asks.amount,
                asksPrice = asks.price
        )
    }

    data class OrderBookString(val amount: String, val price: String)

    /**
     * Convert order book list to string that can be displayed in the UI.
     */
    private fun fromOrderBookMapToString(map: HashMap<Double, OrderBookLevel>): OrderBookString {
        val list = map.toSortedMap().values.toList().reversed()

        val amount = StringBuilder()
        val price = StringBuilder()

        list.forEach {
            amount.append(String.format("%.2f", it.amount.absoluteValue))
            amount.append("\n")

            price.append(String.format("%.2f", it.price))
            price.append("\n")
        }

        return OrderBookString(amount = amount.toString(), price = price.toString())
    }

}
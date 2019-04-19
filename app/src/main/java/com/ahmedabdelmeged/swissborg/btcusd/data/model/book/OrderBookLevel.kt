package com.ahmedabdelmeged.swissborg.btcusd.data.model.book

/**
 * The order book level. It's represent one Update for a book price.
 */
data class OrderBookLevel(
        val price: Double,
        val count: Int,
        val amount: Double) {

    companion object {
        /**
         * Convert list of book level doubles from the API to [OrderBookLevel] so we can do
         * operations on it easily.
         */
        fun fromList(array: List<Double>): OrderBookLevel {
            return OrderBookLevel(
                    price = array[0],
                    count = array[1].toInt(),
                    amount = array[2]
            )
        }
    }

}
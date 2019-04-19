package com.ahmedabdelmeged.swissborg.btcusd.data.model.book

/**
 * The data representation of [OrderBook].
 */
data class OrderBookModel(
        val asks: HashMap<Double, OrderBookLevel> = hashMapOf(),
        val bids: HashMap<Double, OrderBookLevel> = hashMapOf()) {

    /**
     * Update the current order book level. If prices is removed or added to any side.
     *
     * How this work?
     * Let's consider we will get 50 items in the first WebSocket response mapped to [OrderBookLevel]
     * we will store them in map with the price as the key and [OrderBookLevel] as value. Now the WebSocket
     * will send a new update that will contain one [OrderBookLevel] we will check if the [OrderBookLevel.count]
     * is zero if so remove it from the map otherwise add it. We will also check which side the change
     * happen in bids or asks depending on the value of [OrderBookLevel.amount]
     *
     * Example:
     * The WebSocket return [OrderBookLevel(5000.8, 1, 0.25), OrderBookLevel(5100.8, 2, 1.25) ...etc]
     * in the first response. Now the bids map will have 2+n items.
     * The WebSocket send a new update like this OrderBookLevel(5000.8, 0, 0.25). Now the count is
     * zero so we will remove this level from the bids map. if the count > 0 we will add the level
     * to the bids map. Same thing apply for the asks map.
     */
    fun updateOrderBookLevel(level: OrderBookLevel): OrderBookModel {
        return if (level.amount < 0) {
            OrderBookModel(asks = updateSideMap(level, asks), bids = bids)
        } else {
            OrderBookModel(asks = asks, bids = updateSideMap(level, bids))
        }
    }

    private fun updateSideMap(level: OrderBookLevel, side: HashMap<Double, OrderBookLevel>): HashMap<Double, OrderBookLevel> {
        val newMap = side.toMutableMap()
        val shouldDelete = level.count == 0

        newMap.remove(level.price)
        if (!shouldDelete) {
            newMap[level.price] = level.copy()
        }

        return newMap as HashMap<Double, OrderBookLevel>
    }

    companion object {
        /**
         * Create a new order book from the first WebSocket response.
         * We make the level price as key here so we can track of the level update.
         */
        fun createFromBookOrderLevels(orderBooksLevels: List<OrderBookLevel>): OrderBookModel {
            val asks = hashMapOf<Double, OrderBookLevel>()
            val bids = hashMapOf<Double, OrderBookLevel>()

            orderBooksLevels.filter { it.count > 0 }.forEach {
                if (it.amount > 0) {
                    bids[it.price] = it
                } else {
                    asks[it.price] = it
                }
            }

            return OrderBookModel(asks, bids)
        }
    }

}
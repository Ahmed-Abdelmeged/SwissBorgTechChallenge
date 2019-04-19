package com.ahmedabdelmeged.swissborg.btcusd.data.model.book

import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.OrderBookMapper

/**
 * UI representation of [OrderBookModel] mapped using [OrderBookMapper].
 */
data class OrderBook(val bidsAmount: String,
                     val bidsPrice: String,
                     val asksAmount: String,
                     val asksPrice: String)
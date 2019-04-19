package com.ahmedabdelmeged.swissborg.btcusd.data.repository

import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.OrderBookMapper
import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.TickerMapper
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBook
import com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker.Ticker
import com.ahmedabdelmeged.swissborg.btcusd.data.source.BitfinexDataSource
import com.ahmedabdelmeged.swissborg.btcusd.util.SchedulerProvider
import com.ahmedabdelmeged.swissborg.btcusd.viewmodel.MainViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Repository to interact with [BitfinexDataSource] to provide a clean interface for [MainViewModel].
 */
class BitfinexRepository @Inject constructor(private val source: BitfinexDataSource,
                                             private val schedulerProvider: SchedulerProvider,
                                             private val tickerMapper: TickerMapper,
                                             private val orderBookMapper: OrderBookMapper) {

    fun subscribeToBitfinexChannels(): Disposable {
        return source.subscribeToBitfinexChannels()
    }

    fun tickerObservable(): Observable<Ticker> {
        return source.tickerObservable()
                .map(tickerMapper)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    fun orderBookObservable(): Observable<OrderBook> {
        return source.orderBookObservable()
                .map(orderBookMapper)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

}
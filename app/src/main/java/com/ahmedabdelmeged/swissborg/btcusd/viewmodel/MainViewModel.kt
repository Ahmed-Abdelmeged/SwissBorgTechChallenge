package com.ahmedabdelmeged.swissborg.btcusd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBook
import com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker.Ticker
import com.ahmedabdelmeged.swissborg.btcusd.data.repository.BitfinexRepository
import com.ahmedabdelmeged.swissborg.btcusd.ui.MainFragment
import com.ahmedabdelmeged.swissborg.btcusd.extensions.toLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

/**
 * [ViewModel] for [MainFragment].
 */
class MainViewModel @Inject constructor(repository: BitfinexRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    /**
     * When the ViewModel is initialized in the fragment. Request a subscription to Bitfinex
     * WebSocket channels so we get updates for the ticker and order book.
     */
    init {
        compositeDisposable += repository.subscribeToBitfinexChannels()
    }

    val ticker: LiveData<Ticker> = repository.tickerObservable()
            .toLiveData(compositeDisposable)

    val orderBook: LiveData<OrderBook> = repository.orderBookObservable()
            .toLiveData(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
package com.ahmedabdelmeged.swissborg.btcusd.data.repository

import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.OrderBookMapper
import com.ahmedabdelmeged.swissborg.btcusd.data.mapper.TickerMapper
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBook
import com.ahmedabdelmeged.swissborg.btcusd.data.model.book.OrderBookModel
import com.ahmedabdelmeged.swissborg.btcusd.data.model.ticker.Ticker
import com.ahmedabdelmeged.swissborg.btcusd.data.source.BitfinexDataSource
import com.ahmedabdelmeged.swissborg.btcusd.util.SchedulerProvider
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BitfinexRepositoryTest {

    private val source = mock<BitfinexDataSource>()
    private val schedulerProvider = mock<SchedulerProvider>()
    private val tickerMapper = mock<TickerMapper>()
    private val orderBookMapper = mock<OrderBookMapper>()

    private val repository = BitfinexRepository(source, schedulerProvider, tickerMapper, orderBookMapper)

    @Before
    fun setup() {
        whenever(schedulerProvider.io()).thenReturn(Schedulers.trampoline())
        whenever(schedulerProvider.ui()).thenReturn(Schedulers.trampoline())
    }

    @Test
    fun subscribeToBitfinexChannelsCallsSource() {
        repository.subscribeToBitfinexChannels()
        verify(source, times(1)).subscribeToBitfinexChannels()
    }

    @Test
    fun tickerObservableReturnData() {
        val ticker = Ticker(
                change = 0.0,
                lastPrice = 236.52,
                volume = 5191.36754297,
                high = 250.01,
                low = 220.05
        )

        //Stub mapper
        whenever(tickerMapper.apply(any()))
                .thenReturn(ticker)

        //Stub data source
        whenever(source.tickerObservable())
                .thenReturn(Observable.just(listOf()))

        val testObserver = repository.tickerObservable().test()
        testObserver.assertValue(ticker)
    }

    @Test
    fun orderBookObservableReturnData() {
        val orderBook = OrderBook(
                bidsAmount = "0.40\n0.50\n",
                bidsPrice = "5100.00\n5000.00\n",
                asksAmount = "0.40\n0.50\n",
                asksPrice = "4100.00\n4000.00\n")

        //Stub mapper
        whenever(orderBookMapper.apply(any()))
                .thenReturn(orderBook)

        //Stub data source
        whenever(source.orderBookObservable())
                .thenReturn(Observable.just(OrderBookModel(hashMapOf(), hashMapOf())))

        val testObserver = repository.orderBookObservable().test()
        testObserver.assertValue(orderBook)
    }

}
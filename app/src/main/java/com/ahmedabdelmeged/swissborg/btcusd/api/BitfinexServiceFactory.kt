package com.ahmedabdelmeged.swissborg.btcusd.api

import android.app.Application
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.BackoffStrategy
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.ahmedabdelmeged.swissborg.btcusd.di.module.ApiModule
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter

/**
 * Factory class to create [BitfinexService]. Used in [ApiModule].
 */
object BitfinexServiceFactory {

    fun makeBitfinexService(application: Application, isDebug: Boolean): BitfinexService {
        val okHttpClient = makeOkHttpClient(
                makeLoggingInterceptor((isDebug))
        )
        return makeBitfinexService(okHttpClient, application)
    }

    private fun makeBitfinexService(okHttpClient: OkHttpClient, application: Application): BitfinexService {
        return Scarlet.Builder()
                .webSocketFactory(okHttpClient.newWebSocketFactory(BitfinexApiConstants.BITFINEX_WEB_SOCKET_BASE_URL))
                .addMessageAdapterFactory(GsonMessageAdapter.Factory())
                .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
                .backoffStrategy(makeBackoffStrategy())
                .lifecycle(makeSocketLifeCycle(application))
                .build()
                .create()
    }

    private fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()
    }

    /**
     * Logger for the OKHttp client requests and responses.
     */
    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    /**
     * The lifecycle of the WebSocket since we have only one UI currently we will make it live
     * as long as the application in the foreground we can modify that in a production application.
     */
    private fun makeSocketLifeCycle(application: Application): Lifecycle {
        return AndroidLifecycle.ofApplicationForeground(application)
    }

    /**
     * Used to customize how often Scarlet retries WebSocket connections. Incase of the connection
     * failed or there is no internet connection(Network change resiliency).
     */
    private fun makeBackoffStrategy(): BackoffStrategy {
        return ExponentialWithJitterBackoffStrategy(5000, 5000)
    }

}
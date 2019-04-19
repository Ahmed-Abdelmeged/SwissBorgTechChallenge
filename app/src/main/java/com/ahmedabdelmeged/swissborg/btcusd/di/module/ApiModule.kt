package com.ahmedabdelmeged.swissborg.btcusd.di.module

import android.app.Application
import com.ahmedabdelmeged.swissborg.btcusd.BuildConfig
import com.ahmedabdelmeged.swissborg.btcusd.api.BitfinexService
import com.ahmedabdelmeged.swissborg.btcusd.api.BitfinexServiceFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApiModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provicesBitfinexService(application: Application): BitfinexService =
            BitfinexServiceFactory.makeBitfinexService(application, BuildConfig.DEBUG)

}
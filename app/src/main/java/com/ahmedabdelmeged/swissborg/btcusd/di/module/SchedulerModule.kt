package com.ahmedabdelmeged.swissborg.btcusd.di.module

import com.ahmedabdelmeged.swissborg.btcusd.util.AppSchedulerProvider
import com.ahmedabdelmeged.swissborg.btcusd.util.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SchedulerModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provicesSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

}
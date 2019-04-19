package com.ahmedabdelmeged.swissborg.btcusd.di

import android.app.Application
import com.ahmedabdelmeged.swissborg.btcusd.SwissBorgApp
import com.ahmedabdelmeged.swissborg.btcusd.di.module.ApiModule
import com.ahmedabdelmeged.swissborg.btcusd.di.module.FragmentModule
import com.ahmedabdelmeged.swissborg.btcusd.di.module.SchedulerModule
import com.ahmedabdelmeged.swissborg.btcusd.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ApiModule::class,
    FragmentModule::class, ViewModelModule::class, SchedulerModule::class])
interface AppComponent : AndroidInjector<SwissBorgApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}
package com.ahmedabdelmeged.swissborg.btcusd

import com.ahmedabdelmeged.swissborg.btcusd.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class SwissBorgApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        //Using timber for logging so we have a central place to log every thing to the logging services.
        //For example Crashlytics, Firebase Analytics...etc.
        //Or disable logging if we want.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

}
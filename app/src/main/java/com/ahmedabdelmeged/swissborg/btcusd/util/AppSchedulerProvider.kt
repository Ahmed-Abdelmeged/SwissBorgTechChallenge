package com.ahmedabdelmeged.swissborg.btcusd.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AppSchedulerProvider : SchedulerProvider {

    override fun io(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun ui(): Scheduler {
        return Schedulers.io()
    }

}
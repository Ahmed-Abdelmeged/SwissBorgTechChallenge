package com.ahmedabdelmeged.swissborg.btcusd.util

import io.reactivex.Scheduler

interface SchedulerProvider {

    fun io(): Scheduler

    fun ui(): Scheduler

}
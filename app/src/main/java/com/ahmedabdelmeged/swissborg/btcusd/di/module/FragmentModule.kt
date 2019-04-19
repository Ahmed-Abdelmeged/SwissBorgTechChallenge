package com.ahmedabdelmeged.swissborg.btcusd.di.module

import com.ahmedabdelmeged.swissborg.btcusd.ui.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun mainFrgamnt(): MainFragment

}
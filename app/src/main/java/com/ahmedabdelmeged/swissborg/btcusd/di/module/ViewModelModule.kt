package com.ahmedabdelmeged.swissborg.btcusd.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmedabdelmeged.swissborg.btcusd.di.ViewModelKey
import com.ahmedabdelmeged.swissborg.btcusd.viewmodel.MainViewModel
import com.ahmedabdelmeged.swissborg.btcusd.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}
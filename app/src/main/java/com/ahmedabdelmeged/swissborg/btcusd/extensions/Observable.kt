package com.ahmedabdelmeged.swissborg.btcusd.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

/**
 * Convert Rx [Observable] to [LiveData]. We do so because [LiveData] is better in the UI layer
 * since it's a lifecycle aware it will only send updates when the UI is active and also play nice
 * with the data binding library.
 *
 * Better call this method in the [ViewModel] when all the work on the [Observable] is done and ready
 * to emit it's items to the UI. DON'T FORGET to pass [CompositeDisposable] so we don't leak the
 * subscription of the [Observable] and clean it in [ViewModel.onCleared].
 */
fun <T> Observable<T>.toLiveData(compositeDisposable: CompositeDisposable): LiveData<T> {
    val liveData = MutableLiveData<T>()
    compositeDisposable += subscribe({ liveData.postValue(it) }, { error -> Timber.e(error) })
    return liveData
}
package com.treefrogapps.javafx.rxjava3

import com.treefrogapps.rxjava3.rxSubscriber
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import javafx.beans.InvalidationListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object ObservableListFlowable {

    class DisposableObservableList<T> : ObservableList<T> by FXCollections.synchronizedObservableList(FXCollections.emptyObservableList<T>()) {

        private var disposable: Disposable? = null
        fun setDisposable(disposable: Disposable) {
            this.disposable?.dispose()
            this.disposable = disposable
        }

        fun dispose() {
            this.disposable?.dispose()
            this.disposable = null
        }
    }


    /**
     * Adapts the given [Flowable] to a ReactiveStreams [ObservableList].
     */
    fun <T> Flowable<T>.toObservableList(): DisposableObservableList<T> =
        DisposableObservableList<T>()
            .apply { rxSubscriber(next = { clear(); addAll(it) }).let(this::setDisposable) }


    /**
     * Adapts the given [ObservableList] to a [Flowable].
     */
    fun <T> ObservableList<T>.toFlowable(strategy: BackpressureStrategy = LATEST): Flowable<List<T>> =
        Flowable.create({ e ->
                            val l = InvalidationListener { e.onNext(this.toList()) }
                            addListener(l)
                            e.setCancellable { removeListener(l) }
                        }, strategy)
}
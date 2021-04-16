package com.treefrogapps.javafx.rxjava3

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

object ObservableValueFlowable {

    data class ChangeEvent<T>(val oldValue: T, val newValue: T)

    /**
     * Adapts the given [ObservableValue] to a ReactiveStreams [Flowable].
     */
    fun <T> ObservableValue<T>.toValueFlowable(strategy: BackpressureStrategy = LATEST): Flowable<T> =
        Flowable.create({ e ->
                            val l = InvalidationListener { e.onNext(value) }
                            addListener(l)
                            e.setCancellable { removeListener(l) }
                        }, strategy)

    /**
     * Adapts the given [ObservableValue] to a [ChangeEvent] [Flowable].
     */
    fun <T> ObservableValue<T>.toChangeFlowable(strategy: BackpressureStrategy = LATEST): Flowable<ChangeEvent<T>> =
        Flowable.create({ e ->
                            val l: ChangeListener<T> = ChangeListener<T> { _, o, n -> e.onNext(ChangeEvent(o, n)) }
                            addListener(l)
                            e.setCancellable { removeListener(l) }
                        }, strategy)
}
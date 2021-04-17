package com.treefrogapps.javafx.rxjava3

import com.treefrogapps.rxjava3.rxSubscriber
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import javafx.beans.InvalidationListener
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.synchronizedObservableList
import javafx.collections.ObservableList
import java.util.function.Predicate
import java.util.function.UnaryOperator

object ObservableListFlowable {

    interface DisposableList {
        fun dispose()
    }

    open class MutableDisposableObservableList<T> internal constructor() : DisposableList,
        ObservableList<T> by synchronizedObservableList(observableArrayList<T>()) {

        private var disposable: Disposable? = null

        internal fun setDisposable(disposable: Disposable) {
            this.disposable?.dispose()
            this.disposable = disposable
        }

        override fun dispose() {
            this.disposable?.dispose()
            this.disposable = null
        }
    }

    class DisposableObservableList<T> internal constructor(private val delegate: MutableDisposableObservableList<T>)
        : DisposableList, ObservableList<T> by delegate {

        override fun dispose() = delegate.dispose()

        override fun sort(c: Comparator<in T>?) = throwError()
        override fun replaceAll(operator: UnaryOperator<T>) = throwError()
        override fun retainAll(vararg elements: T): Boolean = throwError()
        override fun retainAll(elements: Collection<T>): Boolean = throwError()
        override fun removeAt(index: Int): T = throwError()
        override fun removeAll(vararg elements: T): Boolean = throwError()
        override fun removeAll(elements: Collection<T>): Boolean = throwError()
        override fun remove(from: Int, to: Int) = throwError()
        override fun remove(element: T): Boolean = throwError()
        override fun addAll(index: Int, elements: Collection<T>): Boolean = throwError()
        override fun addAll(vararg elements: T): Boolean = throwError()
        override fun addAll(elements: Collection<T>): Boolean = throwError()
        override fun add(index: Int, element: T) = throwError()
        override fun add(element: T): Boolean = throwError()
        override fun removeIf(filter: Predicate<in T>): Boolean = throwError()
        override fun clear() = throwError()

        private fun throwError(): Nothing = throw IllegalStateException("Read Only Collection")
    }

    /**
     * Adapts the given [Flowable] to a ReactiveStreams [ObservableList].
     */
    fun <T> Flowable<List<T>>.toObservableList(): DisposableObservableList<T> =
        MutableDisposableObservableList<T>()
            .apply { rxSubscriber(next = { clear(); addAll(it) }).let(this::setDisposable) }
            .let(::DisposableObservableList)

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
package com.treefrogapps.javafx.rxjava3

import com.treefrogapps.rxjava3.rxSubscriber
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.BackpressureStrategy.LATEST
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import javafx.beans.InvalidationListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableValue

object ObservableValueFlowable {

    data class ChangeEvent<T>(val oldValue: T, val newValue: T)

    class MutableObservableString internal constructor(private val delegate: StringProperty = SimpleStringProperty(""))
        : Disposable, WritableValue<String> by delegate, ObservableValue<String> by delegate {

        private var disposable: Disposable? = null

        internal fun setDisposable(disposable: Disposable) {
            this.disposable?.dispose()
            this.disposable = disposable
        }

        override fun dispose() {
            this.disposable?.dispose()
            this.disposable = null
        }

        override fun isDisposed(): Boolean = disposable?.isDisposed == true

        override fun getValue(): String = delegate.value
    }

    class ObservableString internal constructor(private val delegate: MutableObservableString)
        : Disposable, ObservableValue<String> by delegate {

        override fun dispose() = delegate.dispose()
        override fun isDisposed(): Boolean = delegate.isDisposed
    }

    class MutableObservableBoolean internal constructor(private val delegate: BooleanProperty = SimpleBooleanProperty(false))
        : Disposable, WritableValue<Boolean> by delegate, ObservableValue<Boolean> by delegate {

        private var disposable: Disposable? = null

        internal fun setDisposable(disposable: Disposable) {
            this.disposable?.dispose()
            this.disposable = disposable
        }

        override fun dispose() {
            this.disposable?.dispose()
            this.disposable = null
        }

        override fun isDisposed(): Boolean = disposable?.isDisposed == true

        override fun getValue(): Boolean = delegate.value
    }

    class ObservableBoolean internal constructor(private val delegate: MutableObservableBoolean)
        : Disposable, ObservableValue<Boolean> by delegate {

        override fun dispose() = delegate.dispose()
        override fun isDisposed(): Boolean = delegate.isDisposed
    }

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

    fun Flowable<String>.toObservableString(bindable: StringProperty? = null): ObservableString = toObservableString(bindable) { it }

    fun <T> Flowable<T>.toObservableString(bindable: StringProperty? = null, adapter: (T) -> String): ObservableString =
        MutableObservableString()
            .apply { map { adapter(it) }.rxSubscriber(next = { value = it }).let(this::setDisposable) }
            .let(::ObservableString)
            .apply { bindable?.bind(this) }

    fun Flowable<Boolean>.toObservableBoolean(bindable: BooleanProperty? = null): ObservableBoolean= toObservableBoolean(bindable) { it }

    fun <T> Flowable<T>.toObservableBoolean(bindable: BooleanProperty? = null, adapter: (T) -> Boolean): ObservableBoolean =
        MutableObservableBoolean()
            .apply { map { adapter(it) }.rxSubscriber(next = { value = it }).let(this::setDisposable) }
            .let(::ObservableBoolean)
            .apply { bindable?.bind(this) }
}
package com.treefrogapps.javafx.dagger

import dagger.MapKey
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass


class InitializableInjector @Inject constructor(private val injectors: MutableMap<Class<out DaggerInitializable>, Provider<InitializableComponentBuilder<DaggerInitializable>>>) {

    @MapKey annotation class InitializableKey(val value: KClass<out DaggerInitializable>)

    fun inject(t: DaggerInitializable) {
        injectors.getValue(t.javaClass).get()
                .create(t)
                .inject(t)
    }
}
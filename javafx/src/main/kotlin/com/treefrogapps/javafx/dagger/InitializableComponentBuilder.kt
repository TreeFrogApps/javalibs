package com.treefrogapps.javafx.dagger

import dagger.BindsInstance


abstract class InitializableComponentBuilder<T : DaggerInitializable> {

    fun create(instance: T): InitializableComponentInjector<T> {
        seedInstance(instance)
        return build()
    }

    @BindsInstance
    abstract fun seedInstance(instance: T)

    abstract fun build(): InitializableComponentInjector<T>
}
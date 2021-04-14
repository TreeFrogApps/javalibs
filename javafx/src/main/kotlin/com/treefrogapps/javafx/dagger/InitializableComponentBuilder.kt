package com.treefrogapps.javafx.dagger

import dagger.BindsInstance

@Deprecated("Prefer InitializableComponentFactory")
abstract class InitializableComponentBuilder<T : DaggerInitializable> : InitializableComponentFactory<T> {

    override fun create(instance: T): InitializableComponentInjector<T> {
        seedInstance(instance)
        return build()
    }

    @BindsInstance
    abstract fun seedInstance(instance: T)

    abstract fun build(): InitializableComponentInjector<T>
}
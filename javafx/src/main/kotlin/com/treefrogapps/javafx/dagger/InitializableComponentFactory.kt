package com.treefrogapps.javafx.dagger

import dagger.BindsInstance

interface InitializableComponentFactory<T : DaggerInitializable> {

    fun create(@BindsInstance instance: T): InitializableComponentInjector<T>
}
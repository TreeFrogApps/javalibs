package com.treefrogapps.javafx.dagger

interface InitializableComponentInjector<T : DaggerInitializable> {

    fun inject(t: T)
}
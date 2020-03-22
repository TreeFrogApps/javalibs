package com.treefrogapps.javafx.dagger

interface ApplicationInjector<T : DaggerApplication> {

    fun inject(application: T)
}
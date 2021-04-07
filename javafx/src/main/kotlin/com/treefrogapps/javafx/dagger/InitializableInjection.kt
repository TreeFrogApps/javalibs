package com.treefrogapps.javafx.dagger

import javafx.application.Application

object InitializableInjection {

    fun inject(controller: DaggerInitializable, app: Application?) {
        when (app) {
            is HasInitializableInjector -> app.controllerInjector().inject(controller)
            else -> throw IllegalArgumentException("Application class must implement HasInitializableInjector")
        }
    }
}
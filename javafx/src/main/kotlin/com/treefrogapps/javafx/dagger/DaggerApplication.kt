package com.treefrogapps.javafx.dagger

import javafx.application.Application
import javafx.stage.Stage
import javax.inject.Inject

/**
 * Main [Application] super type for all automatic dependency injection subtypes
 */
abstract class DaggerApplication : Application(), HasInitializableInjector {

    @Inject lateinit var initializableInjector: InitializableInjector

    companion object {
        var app: Application? = null
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(primaryStage: Stage) {
        app = this
        (component() as (ApplicationInjector<DaggerApplication>))
            .inject(this)
    }

    override fun controllerInjector(): InitializableInjector = initializableInjector

    protected abstract fun component(): ApplicationInjector<out DaggerApplication>
}
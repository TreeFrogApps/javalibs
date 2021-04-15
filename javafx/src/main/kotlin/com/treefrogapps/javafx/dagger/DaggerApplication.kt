package com.treefrogapps.javafx.dagger

import javafx.application.Application
import javafx.stage.Stage
import javax.inject.Inject

/**
 * Main [Application] super type for all automatic dependency injection subtypes
 */
abstract class DaggerApplication : Application(), HasControllerComponentFactory {

    @Inject lateinit var componentFactory: ControllerComponentFactory

    companion object {
        internal var app: DaggerApplication? = null
    }

    @Suppress("UNCHECKED_CAST")
    override fun start(primaryStage: Stage) {
        app = this
        (component() as (ApplicationInjector<DaggerApplication>))
            .inject(this)
    }

    override fun controllerInjector(): ControllerComponentFactory = componentFactory

    protected abstract fun component(): ApplicationInjector<out DaggerApplication>
}
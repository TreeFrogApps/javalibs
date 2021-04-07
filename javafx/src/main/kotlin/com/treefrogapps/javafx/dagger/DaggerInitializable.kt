package com.treefrogapps.javafx.dagger

import javafx.application.Platform
import javafx.fxml.Initializable
import java.net.URL
import java.util.*

/**
 * Super type for all sub-classes that want automatic injection
 */
abstract class DaggerInitializable : Initializable {

    protected var resources : ResourceBundle? = null

    /**
     * Callback hook method to inject members annotated with [javax.inject.Inject]
     *
     * Note : The hook method is not guaranteed to be called on
     * the JavaFX Application thread, however injection *should* always
     * happen on the main application thread using [Platform.runLater]
     * ensures injection happens on the [javafx.application.Application] thread
     */
    final override fun initialize(location: URL?, resources: ResourceBundle?) {
        Platform.runLater {
            InitializableInjection.inject(this, DaggerApplication.app)
            this.resources = resources
            onInitialized(location)
        }
    }

    /**
     * Hook callback for subtypes to do any first time initialisation, this method will only be called
     * once per instance.
     *
     * Guarantees being called on JavaFX Application thread
     */
    abstract fun onInitialized(location: URL?)
}
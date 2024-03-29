package com.treefrogapps.javafx

import com.treefrogapps.javafx.dagger.DaggerController
import io.reactivex.rxjava3.core.Single
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.util.Callback
import java.net.URL
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to instantiate views.  Android terminology used to ease understanding of what this class is doing
 */
@Singleton
class LayoutInflater @Inject constructor() {

    fun <T : Initializable, V : Parent> inflate(layoutResource: URL, controller: T? = null): V? =
        runCatching {
            createLoader<T>(layoutResource).run {
                setController(controller)
                load<V>()
            }
        }.getOrNull()

    fun <T : Initializable, V : Parent> inflate(layoutResource: URL, callback: Callback<Class<T>, T>? = null): T? =
        runCatching {
            createLoader(layoutResource, callback).run {
                load<V>()
                getController<T>()
            }
        }.getOrNull()

    internal fun <T : DaggerController> inflate(
        controllerFactory: Callback<Class<T>, T>,
        layoutResource: URL,
        bundle: ResourceBundle,
        args: Map<String, String> = mapOf()): Single<Layout> =
        Single.fromCallable { createLoader(layoutResource, controllerFactory, bundle) }
            .map { loader ->
                val root = loader.load<Parent>()
                val controller: LayoutController = loader.getController()
                controller.setArgs(args)
                Layout(controller, root)
            }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Initializable> createLoader(
        location: URL,
        callback: Callback<Class<T>, T>? = null,
        bundle: ResourceBundle? = null): FXMLLoader =
        FXMLLoader(
            location,
            bundle,
            null,
            callback as? Callback<Class<*>, Any>?)
}
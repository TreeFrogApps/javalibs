package com.treefrogapps.javafx

import com.treefrogapps.javafx.dagger.DaggerController
import io.reactivex.rxjava3.core.Single
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.util.Callback
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to instantiate views.  Android terminology used to ease understanding of what this class is doing
 */
@Singleton
class LayoutInflater @Inject constructor() {

    internal fun <T : DaggerController> inflate(
        controllerFactory: Callback<Class<T>, T>,
        layoutResource: String,
        bundle: ResourceBundle,
        args: Map<String, String> = mapOf()): Single<Layout> =
        Single.fromCallable { createLoader(controllerFactory, layoutResource, bundle) }
            .map { loader ->
                val root = loader.load<Parent>()
                val controller: LayoutController = loader.getController()
                controller.setArgs(args)
                Layout(controller, root)
            }

    @Suppress("UNCHECKED_CAST")
    private fun <T : DaggerController> createLoader(
        controllerFactory: Callback<Class<T>, T>,
        layoutResource: String,
        bundle: ResourceBundle): FXMLLoader =
        FXMLLoader(LayoutInflater::class.java.getResource("/layout/${layoutResource}.fxml"),
                   bundle,
                   null,
                   controllerFactory as Callback<Class<*>, Any>)
}
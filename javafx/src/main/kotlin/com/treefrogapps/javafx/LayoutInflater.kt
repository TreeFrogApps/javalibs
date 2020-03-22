package com.treefrogapps.javafx

import io.reactivex.rxjava3.core.Single
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to instantiate views.  Android terminology used to ease understanding of what this class is doing
 */
@Singleton class LayoutInflater @Inject constructor() {

    internal fun inflate(rootLayout: String, bundle: ResourceBundle, args: Map<String, String> = mapOf()): Single<Layout> =
            Single.fromCallable { createLoader(rootLayout, bundle) }
                    .map { loader ->
                        val root = loader.load<Parent>()
                        val controller: LayoutController = loader.getController()
                        controller.setArgs(args)
                        Layout(controller, root)
                    }

    private fun createLoader(rootLayout: String, bundle: ResourceBundle): FXMLLoader =
            FXMLLoader(LayoutInflater::class.java.getResource("/layout/${rootLayout}.fxml"), bundle)
}
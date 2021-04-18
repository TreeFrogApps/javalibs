package com.treefrogapps.javafx.dagger

import javafx.util.Callback
import java.net.URL

/**
 * Interface controller factory [Callback] and layout resource name.
 */
interface ControllerFactory<T : DaggerController> {

    /**
     * The callback to create te controller
     *
     * @return the factory [Callback] to create the controller
     */
    fun  callback(): Callback<Class<T>, T>

    /**
     * The layout for this controller.
     *
     * The layout should be inside resources/layout/ folder and named without extension and without extension.
     * For example : file = /resources/layout/my_layout.fxml, layout resource = my_layout
     *
     * @return layout resource name
     */
    fun layoutLocation(): URL
}
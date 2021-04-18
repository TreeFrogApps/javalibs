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
     * The layout [URL] for this controller from a resource location  ie. /fxml/my_layout.fxml
     *
     * @return layout resource location as [URL]
     */
    fun layoutLocation(): URL
}
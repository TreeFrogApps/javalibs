package com.treefrogapps.javafx.dagger

/**
 * Interface class for supplying SubComponent
 */
interface ControllerComponent<T : DaggerController> {

    /**
     * The [ControllerFactory] that is bound to this Component
     * Implementing clients should add a [Module] annotated class that provides this
     * or use an abstract class that supplies this directly.
     */
    fun controllerFactory(): ControllerFactory<T>

    interface Factory<T : DaggerController> {
        fun create(): ControllerComponent<T>
    }
}
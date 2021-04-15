package com.treefrogapps.javafx.dagger

object ControllerComponentHelper {

    internal fun <T : DaggerController> assistedCreate(controller : Class<T>, hasCreator : HasControllerComponentFactory?) : ControllerComponent<T>? =
        hasCreator?.controllerInjector()?.createControllerComponent(controller)

}
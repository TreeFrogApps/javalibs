package com.treefrogapps.javafx.dagger

import javax.inject.Inject
import javax.inject.Singleton


@Singleton class ControllerComponentFactory @Inject constructor(
    private val injectors: @JvmSuppressWildcards Map<Class<out DaggerController>, ControllerComponent.Factory<out DaggerController>>) {

    @Suppress("UNCHECKED_CAST")
    internal fun <T : DaggerController> createControllerComponent(controller: Class<T>): ControllerComponent<T> =
        injectors
            .getValue(controller)
            .create() as ControllerComponent<T>
}
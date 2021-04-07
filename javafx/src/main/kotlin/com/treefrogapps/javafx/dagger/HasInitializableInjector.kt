package com.treefrogapps.javafx.dagger


interface HasInitializableInjector {

    fun controllerInjector(): InitializableInjector
}
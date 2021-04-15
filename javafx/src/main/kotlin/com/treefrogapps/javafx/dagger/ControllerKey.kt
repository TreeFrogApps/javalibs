package com.treefrogapps.javafx.dagger

import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class ControllerKey(val value: KClass<out DaggerController>)
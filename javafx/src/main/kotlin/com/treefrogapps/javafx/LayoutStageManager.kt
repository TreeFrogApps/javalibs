package com.treefrogapps.javafx

import dagger.MapKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Class to facilitate transitions between [LayoutStage] and [LayoutController] classes.
 *
 * Although this class uses Dependency injection [Singleton] annotation this is not a true singleton.
 * It does not necessarily need to be a singleton, however the provided [MutableMap] should always be the same
 * instance.
 *
 * @param stages map of Stage Class types to their corresponding [LayoutStage] instance
 *
 * */
@Singleton class LayoutStageManager @Inject constructor(private val stages: MutableMap<Class<out LayoutStage>, LayoutStage>) {

    @MapKey annotation class StageKey(val value: KClass<out LayoutStage>)

    /**
     * Launch or update a [LayoutStage] stage, requesting it to show the associated [LayoutController]
     *
     * @param stage the [LayoutStage] to open
     * @param controller the [LayoutController] to inflate or update
     * @param args the arguments that should be passed to the [LayoutController]
     */
    fun <T : LayoutStage, S : LayoutController> launch(stage: Class<T>, controller: Class<S>, args: Map<String, String> = mapOf()) {
        stages[stage]?.updateOrLoad(controller, args)
    }

    /**
     * Close a stage, equivalent of physically pressing the red 'X' on a open window
     */
    fun <T : LayoutStage> closeStage(stage: Class<T>) {
        stages[stage]?.close()
    }
}
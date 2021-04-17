package com.treefrogapps.javafx

import com.treefrogapps.javafx.dagger.DaggerController
import javafx.stage.Stage

/**
 * Super type for all sub-classes that should be included for
 * lifecycle callbacks
 * [onUpdate]
 * [onStart]
 * [onStop]
 */
abstract class LayoutController : DaggerController() {

    protected val args: MutableMap<String, String> = mutableMapOf()

    fun setArgs(args: Map<String, String>) {
        this.args.run { clear(); putAll(args) }
    }

    fun updateArgs(args: Map<String, String>) {
        setArgs(args)
        onUpdate(args)
    }

    /**
     * Called when this [DaggerController] is currently in the foreground
     * but provided new arguments.  This hook can be called more than once per
     * instance and allows for updating arguments in the current instance.
     *
     * @param args new arguments provided to this [DaggerController]
     */
    protected abstract fun onUpdate(args: Map<String, String>)

    /**
     * Called after [onInitialized] and before [onStop]. This will only be called once per instance
     */
    abstract fun onStart(parent : Stage)

    /**
     * Called after [onStart]. This will only be called once per instance
     *
     * This hook method should be used to do any cleanup on the current [DaggerController]
     * as it is no longer in the foreground either because the window has been closed, or another
     * [DaggerController] is now in the foreground.
     */
    abstract fun onStop()
}
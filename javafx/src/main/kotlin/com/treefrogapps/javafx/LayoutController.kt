package com.treefrogapps.javafx

import com.treefrogapps.javafx.dagger.DaggerInitializable

/**
 * Super type for all sub-classes that should be included for
 * lifecycle callbacks
 * [onUpdate]
 * [onStart]
 * [onStop]
 */
abstract class LayoutController : DaggerInitializable() {

    protected val args: MutableMap<String, String> = mutableMapOf()

    fun setArgs(args: Map<String, String>) {
        this.args.run { clear(); putAll(args) }
    }

    fun updateArgs(args: Map<String, String>) {
        setArgs(args)
        onUpdate(args)
    }

    /**
     * Called when this [LayoutController] is currently in the foreground
     * but provided new arguments.  This hook can be called more than once per
     * instance and allows for updating arguments in the current instance.
     *
     * @param args new arguments provided to this [LayoutController]
     */
    protected abstract fun onUpdate(args: Map<String, String>)

    /**
     * Called after [onInitialized] and before [onStop]. This will only be called once per instance
     */
    abstract fun onStart()

    /**
     * Called after [onStart]. This will only be called once per instance
     *
     * This hook method should be used to do any cleanup on the current [LayoutController]
     * as it is no longer in the foreground either because the window has been closed, or another
     * [LayoutController] is now in the foreground.
     */
    abstract fun onStop()
}
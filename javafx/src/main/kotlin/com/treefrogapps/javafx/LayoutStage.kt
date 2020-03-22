package com.treefrogapps.javafx


import com.treefrogapps.core.extensions.ifNull
import com.treefrogapps.rxjava3.Rx3Schedulers
import com.treefrogapps.rxjava3.plusAssign
import com.treefrogapps.rxjava3.rxSubscriber
import com.treefrogapps.rxjava3.withSchedulers
import dagger.MapKey
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * This class allows control of the JavaFX Application user interface and should be used to conduct all UI transitions
 *
 * A [Stage] is a container for a [Scene] which contains replaceable content.  A [Stage] and [Scene] in this
 * implementation have a 1:1 relationship and [Scene] to (view/activity)[LayoutController] have a 1:n relationship
 *
 * Note : Unlike Android mobile platform, which has the concept of Fragments and Activities, this is really amalgamated
 * in one as the concept on [LayoutController] which has a class and fxml layout associated with it.  Also unlike Android
 * this implementation, so far, has no concept of task stacks.  This *could* be implemented but currently is not.
 */
open class LayoutStage(private val controllers: MutableMap<Class<out LayoutController>, String>,
                       protected val inflater: LayoutInflater,
                       protected val schedulers: Rx3Schedulers,
                       protected val bundle: ResourceBundle,
                       style: StageStyle = StageStyle.DECORATED) : Stage(style) {

    @MapKey annotation class ControllerKey(val value: KClass<out LayoutController>)

    private val currentController: AtomicReference<LayoutController> = AtomicReference()
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        setOnHidden { onClose() }
    }

    fun <T : LayoutController> updateOrLoad(controller: Class<T>, args: Map<String, String>) {
        getMatchingController(controller)?.updateArgs(args)
                .ifNull { loadLayout(controller, args) }
    }

    protected fun updateCurrentController(args: Map<String, String>) {
        currentController.get()?.updateArgs(args)
    }

    private fun <T : LayoutController> loadLayout(controller: Class<T>, args: Map<String, String>) {
        disposable += controllers[controller]
                ?.let { inflater.inflate(it, bundle, args) }
                ?.withSchedulers(schedulers.io(), schedulers.main())
                ?.rxSubscriber(this::changeLayout)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : LayoutController> getMatchingController(controller: Class<T>): T? =
            currentController.get()?.run { if (controller.isInstance(this)) this as T else null }

    private fun onClose() {
        disposable.clear()
        currentController.get()?.onStop()
    }

    private fun changeLayout(layout: Layout) {
        with(layout) {
            currentController.getAndSet(controller)?.onStop()
            currentController.get().onStart()
            scene?.let { it.root = root }.ifNull { scene = Scene(root); show() }
        }
    }
}





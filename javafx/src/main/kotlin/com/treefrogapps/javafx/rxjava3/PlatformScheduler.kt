package com.treefrogapps.javafx.rxjava3

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.EmptyDisposable
import io.reactivex.rxjava3.internal.schedulers.ScheduledRunnable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import javafx.application.Platform
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

internal object PlatformScheduler : Scheduler() {

    @JvmStatic private val executor = Executors.newSingleThreadScheduledExecutor { Thread(it, "JavaFXScheduler-worker") }

    override fun createWorker(): Worker =
        JavaFXWorker()

    override fun shutdown() {
        executor.shutdown()
    }

    private class JavaFXWorker : Worker() {
        private val tasks = CompositeDisposable()
        @Volatile private var disposed = false

        override fun isDisposed(): Boolean = disposed

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable =
                when (disposed) {
                    true -> EmptyDisposable.INSTANCE
                    else -> ScheduledRunnable(
                        JavaFXRunnable(
                            RxJavaPlugins.onSchedule(run)
                        ), tasks)
                            .run {
                                try {
                                    setFuture(when {
                                        delay <= 0L -> executor.submit(this)
                                        else        -> executor.schedule(this, delay, unit)
                                    })
                                } catch (ex: RejectedExecutionException) {
                                    dispose()
                                    RxJavaPlugins.onError(ex)
                                    return EmptyDisposable.INSTANCE
                                }
                                return this
                            }
                }

        override fun dispose() {
            if (!disposed) {
                disposed = true
                tasks.dispose()
            }
        }
    }

    private class JavaFXRunnable(private var task: Runnable?) : Runnable {
        override fun run() {
            try {
                task?.run { Platform.runLater(this) }
            } catch (t: Throwable) {
                RxJavaPlugins.onError(t)
            }
        }
    }
}
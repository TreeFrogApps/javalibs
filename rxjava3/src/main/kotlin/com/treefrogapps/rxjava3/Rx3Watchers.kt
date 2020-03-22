package com.treefrogapps.rxjava3

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER
import io.reactivex.rxjava3.core.Flowable
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent.Kind
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

object Rx3Watchers {

    /**
     * File Watcher that checks if a file or directory is either [ENTRY_CREATE], [ENTRY_CREATE] or [ENTRY_MODIFY].
     *
     * Note : [java.nio.file.WatchService.take] is a blocking call, observers should use appropriate
     * [io.reactivex.rxjava3.schedulers.Schedulers] to subscribe to these events.
     *
     * @param file the location to observe
     * @param strategy [BackpressureStrategy] to adopt for faster producers over consumers
     *
     * @return [Flowable] of events that are either either [ENTRY_CREATE], [ENTRY_CREATE] or [ENTRY_MODIFY].
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun rx3FileWatcher(file: File, strategy: BackpressureStrategy = BUFFER): Flowable<Kind<Path>> =
            Flowable.create({ emitter ->
                                val path = file.toPath()
                                try {
                                    val watcher = path.fileSystem.newWatchService()
                                    val key = path.register(watcher, arrayOf(ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY))
                                    emitter.setCancellable { watcher?.use { it.close() }; key?.cancel() }
                                    while (!emitter.isCancelled) {
                                        with(watcher.take()) {
                                            pollEvents().asSequence()
                                                    .distinct()
                                                    .filter { (it.context() as Path).endsWith(path) }
                                                    .map { it.kind() as Kind<Path> }
                                                    .forEach(emitter::onNext)
                                            reset()
                                        }
                                    }
                                } catch (e: Exception) {
                                    emitter.onError(e)
                                }
                            }, strategy)

    /**
     * [Preferences] watcher that emits [PreferenceChangeEvent] when a event on the provided [Preferences] occurs
     *
     * @param prefs [Preferences] to monitor
     * @param strategy [BackpressureStrategy] to adopt for faster producers over consumers
     * @param filter function applied to filter events
     */
    @JvmStatic fun rx3PreferencesWatcher(prefs: Preferences,
                                         strategy: BackpressureStrategy = BUFFER,
                                         filter: (key: String) -> Boolean = { true }): Flowable<PreferenceChangeEvent> =
            Flowable.create({ emitter ->
                                val listener = PreferenceChangeListener { p0 -> p0?.takeIf { filter(it.key) }?.run { emitter.onNext(this) } }
                                prefs.addPreferenceChangeListener(listener)
                                emitter.setCancellable { prefs.removePreferenceChangeListener(listener) }
                            }, strategy)
}
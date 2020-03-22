package com.treefrogapps.javafx.rxjava3

import io.reactivex.rxjava3.core.Scheduler

object JavaFXSchedulers {

    @JvmStatic fun mainThread(): Scheduler = PlatformScheduler

    @JvmStatic fun shutDown() = PlatformScheduler.shutdown()
}
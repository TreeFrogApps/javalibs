package com.treefrogapps.rxjava3

import io.reactivex.rxjava3.core.Scheduler
import java.util.concurrent.Executor

interface Rx3Schedulers {

    fun io() : Scheduler
    fun computation() : Scheduler
    fun main() : Scheduler
    fun new() : Scheduler
    fun single() : Scheduler
    fun from(executor : Executor) : Scheduler
}
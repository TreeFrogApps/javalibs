package com.treefrogapps.rxjava3

import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.exceptions.CompositeException
import org.apache.logging.log4j.LogManager
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicInteger


fun <T> Observable<T>.withSchedulers(subscribe: Scheduler, observe: Scheduler): Observable<T> =
        compose { it.subscribeOn(subscribe).observeOn(observe) }

fun <T> Flowable<T>.withSchedulers(subscribe: Scheduler, observe: Scheduler): Flowable<T> =
        compose { it.subscribeOn(subscribe).observeOn(observe) }

fun <T> Single<T>.withSchedulers(subscribe: Scheduler, observe: Scheduler): Single<T> =
        compose { it.subscribeOn(subscribe).observeOn(observe) }

fun <T> Maybe<T>.withSchedulers(subscribe: Scheduler, observe: Scheduler): Maybe<T> =
        compose { it.subscribeOn(subscribe).observeOn(observe) }

fun Completable.withSchedulers(subscribe: Scheduler, observe: Scheduler): Completable =
        compose { it.subscribeOn(subscribe).observeOn(observe) }

fun <T> Flowable<T>.rxRetryOnError(maxCount: Int, exponent: Long, scheduler: Scheduler): Flowable<T> =
        retryWhen {
            val int = AtomicInteger(0)
            it.flatMap { throwable ->
                if (int.incrementAndGet() <= maxCount) {
                    Flowable.timer(int.get() * exponent, SECONDS, scheduler)
                } else Flowable.error(throwable)
            }
        }

fun <T> Observable<T>.rxRetryOnError(maxCount: Int, exponent: Long, scheduler: Scheduler): Observable<T> =
        retryWhen {
            val int = AtomicInteger(0)
            it.flatMap { throwable ->
                if (int.incrementAndGet() <= maxCount) {
                    Observable.timer(int.get() * exponent, SECONDS, scheduler)
                } else Observable.error(throwable)
            }
        }

fun Completable.rxRetryOnError(maxCount: Int, exponent: Long, scheduler: Scheduler): Completable =
        retryWhen {
            val int = AtomicInteger(0)
            it.flatMap { throwable ->
                if (int.incrementAndGet() <= maxCount) {
                    Flowable.timer(int.get() * exponent, SECONDS, scheduler)
                } else Flowable.error(throwable)
            }
        }

fun <T> Flowable<T>.rxErrorBreadcrumb(): Flowable<T> {
    val crumb = Rx3BreadcrumbException()
    return onErrorResumeNext { t: Throwable -> throw CompositeException(t, crumb) }
}

fun <T> Observable<T>.rxErrorBreadcrumb(): Observable<T> {
    val crumb = Rx3BreadcrumbException()
    return onErrorResumeNext { t: Throwable -> throw CompositeException(t, crumb) }
}

fun <T> Single<T>.rxErrorBreadcrumb(): Single<T> {
    val crumb = Rx3BreadcrumbException()
    return onErrorResumeNext { t: Throwable -> throw CompositeException(t, crumb) }
}

fun <T> Maybe<T>.rxErrorBreadcrumb(): Maybe<T> {
    val crumb = Rx3BreadcrumbException()
    return onErrorResumeNext { t: Throwable -> throw CompositeException(t, crumb) }
}

fun Completable.rxErrorBreadcrumb(): Completable {
    val crumb = Rx3BreadcrumbException()
    return onErrorResumeNext { t: Throwable -> throw CompositeException(t, crumb) }
}

fun <T> Flowable<T>.rxSubscriber(next: (t: T) -> Unit = { },
                                 error: ((t: Throwable) -> Unit)? = null,
                                 errorMessage: String? = null): Disposable =
        subscribe({ next(it) }, { error?.invoke(it); logError(it, errorMessage) })

fun <T> Observable<T>.rxSubscriber(next: (t: T) -> Unit = { },
                                   error: ((t: Throwable) -> Unit)? = null,
                                   errorMessage: String? = null): Disposable =
        subscribe({ next(it) }, { error?.invoke(it); logError(it, errorMessage) })

fun <T> Single<T>.rxSubscriber(success: (t: T) -> Unit = { },
                               error: ((t: Throwable) -> Unit)? = null,
                               errorMessage: String? = null): Disposable =
        subscribe({ success(it) }, { error?.invoke(it); logError(it, errorMessage) })

fun <T> Maybe<T>.rxSubscriber(success: (t: T) -> Unit = { },
                              error: ((t: Throwable) -> Unit)? = null,
                              errorMessage: String? = null): Disposable =
        subscribe({ success(it) }, { error?.invoke(it); logError(it, errorMessage) })

fun Completable.rxSubscriber(func: () -> Unit = { },
                             error: ((t: Throwable) -> Unit)? = null,
                             errorMessage: String? = null): Disposable =
        subscribe({ func() }, { error?.invoke(it); logError(it, errorMessage) })


private fun logError(t: Throwable, errorMessage: String?) {
    LogManager.getLogger().error(errorMessage ?: "", t)
}
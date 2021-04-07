package com.treefrogapps.rxjava3

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable?) {
    disposable?.run { add(this) }
}
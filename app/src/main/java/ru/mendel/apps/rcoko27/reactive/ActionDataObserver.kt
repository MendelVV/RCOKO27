package ru.mendel.apps.rcoko27.reactive

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import ru.mendel.apps.rcoko27.data.ActionData

open class ActionDataObserver(val next: (message: ActionData) -> Unit) : Observer<ActionData> {

    private lateinit var mDisposable: Disposable

    override fun onComplete() {
        mDisposable.dispose()
    }

    override fun onSubscribe(d: Disposable) {
        mDisposable = d
    }

    override fun onNext(message: ActionData) {
        next(message)
    }

    override fun onError(e: Throwable) {

    }
}
package ru.mendel.apps.rcoko27.reactive

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import ru.mendel.apps.rcoko27.api.responses.BaseResponse

class ResponseObserver(val next: (message: BaseResponse) -> Unit) : Observer<BaseResponse> {

    private lateinit var mDisposable: Disposable

    override fun onComplete() {
        mDisposable.dispose()
    }

    override fun onSubscribe(d: Disposable) {
        mDisposable = d
    }

    override fun onNext(message: BaseResponse) {
        next(message)
    }

    override fun onError(e: Throwable) {

    }
}
package ru.mendel.apps.rcoko27.reactive

import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import org.json.JSONObject
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.data.ActionData

object ReactiveSubject {

    private val actionDataSubject: PublishSubject<ActionData> = PublishSubject.create()
    private val responseSubject: PublishSubject<BaseResponse> = PublishSubject.create()

    init {
       // subject.subscribeOn(Schedulers.io())
        //это видимо в другом случае влияет
    }

    fun addSubscribe(observer: ActionDataObserver){
        actionDataSubject.subscribe(observer)
    }

    fun addSubscribe(observer: ActionDataObserver, filter: String){
        //вроде как подписка с фильтром
        actionDataSubject
            .filter{ x -> x.actionName==filter }
            .subscribe(observer)
    }

    fun next(message: ActionData){
        actionDataSubject.onNext(message)
    }


    fun addSubscribe(observer: ResponseObserver){
        responseSubject.subscribe(observer)
    }

    fun addSubscribe(observer: ResponseObserver, filter: String){
        responseSubject
            .filter { x -> x.action==filter}
            .subscribe(observer)
    }

    fun addSubscribe(observer: Observer<BaseResponse>){
        responseSubject.subscribe(observer)
    }

    fun next(message: BaseResponse){
        responseSubject.onNext(message)
    }
}
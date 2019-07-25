package ru.mendel.apps.rcoko27.fragments

import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

abstract class BaseEventFragment : Fragment(){

    lateinit var mActionObserver: ActionDataObserver
    protected val mObservers = mutableListOf<ResponseObserver>()
    protected val mUiHandler = Handler()

    protected open fun error(message: ActionData){
        if (!isResumed) return
        val type = message.data[ActionData.ITEM_TYPE]

        when (type) {
            "auth" -> showMessage(R.string.no_auth)
            "code" -> showMessage(R.string.error_code)
            "network" -> showMessage(R.string.not_connection)
            "get_data" -> showMessage(R.string.error_get_data)
            else -> showMessage(R.string.unknow_error)
        }
    }

    override fun onStart() {
        super.onStart()
        mActionObserver = ActionDataObserver { x->error(x) }
        ReactiveSubject.addSubscribe(mActionObserver, ActionData.ACTION_ERROR)
    }

    override fun onStop() {
        super.onStop()
        mActionObserver.onComplete()
    }

    protected fun showMessage(id: Int){
        mUiHandler.post {
            Snackbar.make(activity!!.findViewById(android.R.id.content),
                id, Snackbar.LENGTH_LONG).show()
        }

    }

    abstract fun subscribe()

    protected fun unsubscribe(){
        for (observer in mObservers){
            observer.onComplete()
        }
        mObservers.clear()
    }
}
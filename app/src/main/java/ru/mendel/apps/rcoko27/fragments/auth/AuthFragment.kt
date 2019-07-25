package ru.mendel.apps.rcoko27.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.auth_fragment.view.*
import org.json.JSONObject
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.activities.MainActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.AutoLoginRequest
import ru.mendel.apps.rcoko27.async.BasicAsync
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.json.JsonSchema
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import java.util.*

class AuthFragment : AbstractAuthFragment() {

    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        when (message.actionName) {
            ActionData.ACTION_TO_MAIN -> toMain()
            ActionData.ACTION_ERROR -> {
                val type = message.data[ActionData.ITEM_TYPE]

                when (type) {
                    "auth" -> showMessage(R.string.no_auth)
                    "code" -> showMessage(R.string.error_code)
                    "network" -> showMessage(R.string.not_connection)
                    else -> showMessage(R.string.unknow_error)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mObserver = ActionDataObserver { x->next(x)}
        ReactiveSubject.addSubscribe(mObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.auth_fragment,container,false)

        view.button_sign_in.setOnClickListener { auth() }

        return view
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }

    private fun auth(){
        //проверяем заполнение полей
        if (view!!.text_email.text.toString()=="" || view!!.text_password.text.toString()==""){
            showMessage(R.string.need_specify_auth_data)
        }else{
            val token = UUID.randomUUID().toString()
            APIHelper.sendAutoLogin(appname = activity!!.packageName,
                email = view!!.text_email.text.toString(),
                password = view!!.text_password.text.toString(),
                token = token)
        }
    }

    private fun toMain(){
        //переходим к главному меню
        QueryPreference.setLogin(activity!!,view!!.text_email.text.toString())
        QueryPreference.setPassword(activity!!,view!!.text_password.text.toString())
        val intent = Intent(activity,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.in_alpha,R.anim.out_alpha)
    }

}
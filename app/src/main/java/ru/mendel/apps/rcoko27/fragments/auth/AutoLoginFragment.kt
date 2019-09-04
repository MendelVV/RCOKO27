package ru.mendel.apps.rcoko27.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.auto_login_fragment.view.*
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

class AutoLoginFragment : AbstractAuthFragment(){

    private var mLogin: String? = null
    private var mPassword: String? = null
    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        //действие
        when (message.actionName){
            ActionData.ACTION_TO_MAIN -> {
                val ver = message.data[ActionData.ITEM_VERIFICATION]?.toInt()?:0
                QueryPreference.setVerification(activity!!, ver)
                toMain()
            }
            ActionData.ACTION_ERROR ->{
                val type = message.data[ActionData.ITEM_TYPE]
                when (type) {
                    "auth" -> {
                        //открываем страницу регистрации
                        showMessage(R.string.no_auth)
                        toReg()
                    }
                    "network" -> {
                        noNetwork()
                        showMessage(R.string.not_connection)
                    }
                    "format" ->{
                        noNetwork()
                        showMessage(R.string.format_error)
                    }
                    else -> {
                        noNetwork()
                        showMessage(R.string.unknow_error)
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLogin = QueryPreference.getLogin(activity!!)
        mPassword = QueryPreference.getPassword(activity!!)
    }

    override fun onStart() {
        super.onStart()
        mObserver = ActionDataObserver{x->next(x)}
        ReactiveSubject.addSubscribe(mObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.auto_login_fragment,container,false)
        view.button_again.setOnClickListener { again() }
        view.button_reg.setOnClickListener { toReg() }
        view.text_sign_in.setOnClickListener{ again() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth()
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }

    private fun noNetwork(){
        view!!.progress_bar.visibility = View.INVISIBLE
        view!!.button_again.visibility = View.VISIBLE
        view!!.button_reg.visibility = View.VISIBLE
    }

    private fun again(){
        view!!.progress_bar.visibility = View.VISIBLE
        view!!.button_again.visibility = View.INVISIBLE
        view!!.button_reg.visibility = View.INVISIBLE
        auth()
    }

    private fun auth(){
        //проверяем заполнение полей
        val token = UUID.randomUUID().toString()
        QueryPreference.setToken(activity!!, token)
        APIHelper.sendAutoLogin(appname = activity!!.packageName,
            email = mLogin!!,
            password = mPassword!!,
            token = token)
    }

    private fun toReg(){
        //код перехода ко второму фрагменту
        val fragment = RegFragment()
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }

    private fun toMain(){
        //переходим к главному меню
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.in_alpha,R.anim.out_alpha)
    }

}
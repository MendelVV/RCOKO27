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
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.AutoLoginRequest
import ru.mendel.apps.rcoko27.async.BasicAsync
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.json.JsonSchema
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject

class AutoLoginFragment : AbstractAuthFragment(){

    private var mLogin: String? = null
    private var mPassword: String? = null
    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        //действие
        when (message.actionName){
            ActionData.ACTION_TO_MAIN -> toMain()
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
    }

    private fun again(){
        view!!.progress_bar.visibility = View.VISIBLE
        view!!.button_again.visibility = View.INVISIBLE
        auth()
    }

    private fun auth(){
        //проверяем заполнение полей

        val request = AutoLoginRequest()
        request.appname = activity!!.packageName
        request.action = "auth"
        request.email = mLogin
        request.password = mPassword
        RcokoClient.autoLogin(request)

/*        val jsonObject = JSONObject()
        jsonObject.put(JsonSchema.Registration.APPNAME, activity!!.packageName)
        jsonObject.put(JsonSchema.Registration.ACTION, "auth")
        jsonObject.put(JsonSchema.Registration.EMAIL, mLogin)
        jsonObject.put(JsonSchema.Registration.PASSWORD, mPassword)

        mTask = LoginAsync(getString(R.string.url_auth))
        mTask.execute(jsonObject)*/

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

    inner class LoginAsync(url_str:String) : BasicAsync(url_str){

        override fun doInBackground(vararg args: JSONObject?): JSONObject? {
            Thread.sleep(1000)
            return super.doInBackground(*args)
        }

        override fun onPostExecute(result: JSONObject?) {
            super.onPostExecute(result)
            if (result==null) return
            val res = result.getString(JsonSchema.Response.RESULT)
            if (res=="error"){
                //такой пользователь уже есть
                val action = ActionData(ActionData.ACTION_ERROR)
                action.data[ActionData.ITEM_TYPE] = result.getString(JsonSchema.Response.TYPE)
                ReactiveSubject.next(action)

            }else if (res=="ok"){
                //если все хорошо то переходим к данным
                val action = ActionData(ActionData.ACTION_TO_MAIN)
                ReactiveSubject.next(action)
            }else{
                //что-то пошло не так
                val action = ActionData(ActionData.ACTION_ERROR)
                action.data[ActionData.ITEM_TYPE] = "unknow"
                ReactiveSubject.next(action)
            }
        }

    }
}
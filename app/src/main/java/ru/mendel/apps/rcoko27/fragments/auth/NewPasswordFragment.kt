package ru.mendel.apps.rcoko27.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_pass_fragment.view.*
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.activities.MainActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import java.util.*

class NewPasswordFragment : AbstractAuthFragment() {

    companion object {

        const val EMAIL = "email"
        const val CODE = "code"

        fun newInstance(email:String, code:String): NewPasswordFragment {
            val fragment = NewPasswordFragment()
            val args = Bundle()
            args.putString(EMAIL,email)
            args.putString(CODE,code)
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var mEmail:String
    private lateinit var mCode:String
    private var mPassword :String?=null
    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        when (message.actionName) {
            ActionData.ACTION_TO_MAIN -> {
                val ver = message.data[ActionData.ITEM_VERIFICATION]?.toInt()?:0
                QueryPreference.setVerification(activity!!, ver)
                toMain()
            }
            ActionData.ACTION_ERROR -> {
                val type = message.data[ActionData.ITEM_TYPE]
                Log.d("MyTag","type=$type")
                when (type) {
                    "code" -> showMessage(R.string.error_code)
                    "network" -> showMessage(R.string.not_connection)
                    else -> showMessage(R.string.unknow_error)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mObserver = ActionDataObserver{x->next(x)}
        ReactiveSubject.addSubscribe(mObserver)
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEmail = arguments!!.getString(EMAIL)
        mCode = arguments!!.getString(CODE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reg_pass_fragment,container,false)

        view.button_reg.setOnClickListener { check() }

        return view
    }

    private fun sendNewPassword(){
        //отправляем новый пароль
        val token = UUID.randomUUID().toString()
        QueryPreference.setToken(activity!!, token)
        mPassword = view!!.text_pass_1.text.toString()
        APIHelper.newPassword(appname = activity!!.packageName,
            token = token,
            email = mEmail,
            code = mCode,
            password = mPassword!!)

    }


    private fun toMain(){
        //все прошло ужачно - открываем основную activity
        QueryPreference.setLogin(activity!!,mEmail)
        QueryPreference.setPassword(activity!!,mPassword)
        //переходим к главному меню
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.in_alpha,R.anim.out_alpha)

    }

    private fun check(){
        //берем два пароля, сравниваем и идем дальше
        val pass1 = view!!.text_pass_1.text.toString()
        val pass2 = view!!.text_pass_2.text.toString()

        if (pass1.length<8){
            showMessage(R.string.short_pass)
            return
        }
        if (pass1!=pass2){
            showMessage(R.string.passwords_not_match)
            return
        }
        sendNewPassword()
    }
}
package ru.mendel.apps.rcoko27.fragments.auth

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_code_fragment.view.*
import org.json.JSONObject
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.RegCodeRequest
import ru.mendel.apps.rcoko27.async.BasicAsync
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.json.JsonSchema
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject

class RegCodeFragment: AbstractAuthFragment(){

    companion object {

        const val EMAIL = "email"
        const val NAME = "name"

        fun newInstance(email: String, name:String) : RegCodeFragment {
            val fragment = RegCodeFragment()
            val args = Bundle()
            args.putString(EMAIL,email)
            args.putString(NAME,name)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mEmail: String
    private lateinit var mName: String

    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        when (message.actionName) {
            ActionData.ACTION_TO_NEXT -> next()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEmail = arguments!!.getString(EMAIL)
        mName = arguments!!.getString(NAME)
    }

    override fun onStart() {
        super.onStart()
        mObserver = ActionDataObserver{x->next(x)}
        ReactiveSubject.addSubscribe(mObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reg_code_fragment,container,false)
        view.button_reg.setOnClickListener { sendCode() }
        return view
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }

    private fun next(){
        //код перехода ко второму фрагменту
        val fragment = RegPasswordFragment.newInstance(
            mEmail,
            mName,
            view!!.text_code.text.toString()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.sharedElementEnterTransition = DetailsTransition()
            fragment.enterTransition = Fade()
            exitTransition = Fade()
            fragment.sharedElementReturnTransition = DetailsTransition()
        }

        activity!!.supportFragmentManager
            .beginTransaction()
            .addSharedElement(view!!.button_reg, "regTransition")//какое view переходит куда
            .replace(R.id.fragment_layout, fragment)
            .addToBackStack(null)
            .commit()

    }

    private fun sendCode(){
        val code = view!!.text_code.text.toString()
        if (code.length==4){
            //отправляем данные на сервер

            val request = RegCodeRequest()
            request.appname = activity!!.packageName
            request.action = "verify_code"
            request.email = mEmail
            request.code = code

            RcokoClient.regCode(request)

/*            val jsonObject = JSONObject()
            jsonObject.put(JsonSchema.Registration.APPNAME, activity!!.packageName)
            jsonObject.put(JsonSchema.Registration.ACTION, "verify_code")
            jsonObject.put(JsonSchema.Registration.EMAIL, mEmail)
            jsonObject.put(JsonSchema.Registration.CODE, code)

            mTask = RegCodeAsync(getString(R.string.url_reg_code))
            mTask.execute(jsonObject)*/

        }else{
            //выводи сообщение что чего-то нехватает
            showMessage(R.string.invalid_code_range)
        }
    }

    inner class RegCodeAsync(url_str:String) : BasicAsync(url_str){

        override fun onPostExecute(result: JSONObject?) {
            super.onPostExecute(result)
            if (result==null) return
            val res = result.getString(JsonSchema.Response.RESULT)
            if (res=="ok"){
                //все хорошо идем дальше
                val action = ActionData(ActionData.ACTION_TO_NEXT)
                ReactiveSubject.next(action)
            }else if (res=="error"){
                val action = ActionData(ActionData.ACTION_ERROR)
                action.data[ActionData.ITEM_TYPE] = result.getString(JsonSchema.Response.TYPE)
                ReactiveSubject.next(action)
            }else{
                val action = ActionData(ActionData.ACTION_ERROR)
                action.data[ActionData.ITEM_TYPE] = "unknow"
                ReactiveSubject.next(action)
            }
        }

    }

}
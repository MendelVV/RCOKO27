package ru.mendel.apps.rcoko27.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.support.design.chip.Chip
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_role_fragment.view.*
import org.json.JSONObject
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.activities.MainActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.RegistrationRequest
import ru.mendel.apps.rcoko27.async.BasicAsync
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.json.JsonSchema
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject

class RegRoleFragment : AbstractAuthFragment() {

    companion object {

        const val EMAIL = "email"
        const val NAME = "name"
        const val CODE = "code"
        const val PASSWORD = "password"

        fun newInstance(email:String, name:String, code:String, password:String): RegRoleFragment {
            val fragment = RegRoleFragment()
            val args = Bundle()
            args.putString(EMAIL,email)
            args.putString(NAME,name)
            args.putString(CODE,code)
            args.putString(PASSWORD,password)
            fragment.arguments = args
            return fragment
        }

    }

    lateinit var mEmail : String
    lateinit var mName : String
    lateinit var mCode : String
    lateinit var mPassword : String
    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        when (message.actionName) {
            ActionData.ACTION_TO_MAIN -> toMain()
            ActionData.ACTION_ERROR -> {
                val type = message.data[ActionData.ITEM_TYPE]

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
        mCode = arguments!!.getString(CODE)
        mPassword = arguments!!.getString(PASSWORD)

    }

    override fun onStart() {
        super.onStart()
        mObserver = ActionDataObserver { x->next(x) }
        ReactiveSubject.addSubscribe(mObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reg_role_fragment,container,false)

        view.chip_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId){
                R.id.chip_student -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled=false
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled=true
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled=true

                }
                R.id.chip_parent -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled=true
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled=false
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled=true

                }
                R.id.chip_worker -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled=true
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled=true
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled=false

                }
            }

            view.button_reg.setOnClickListener { registration() }
//            view.chip_group.checkedChipId
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }

    private fun registration(){

        val role = when (view!!.chip_group.checkedChipId){
            R.id.chip_student -> 0
            R.id.chip_parent -> 1
            R.id.chip_worker -> 2
            else -> -1
        }

        APIHelper.sendRegistration(appname = activity!!.packageName,
            email = mEmail,
            name = mName,
            code = mCode,
            password = mPassword,
            role = role)
    }

    private fun toMain(){
        QueryPreference.setLogin(activity!!,mEmail)
        QueryPreference.setPassword(activity!!,mPassword)
        //переходим к главному меню
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.in_alpha,R.anim.out_alpha)
    }

}
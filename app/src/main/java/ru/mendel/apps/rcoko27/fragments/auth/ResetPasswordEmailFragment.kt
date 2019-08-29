package ru.mendel.apps.rcoko27.fragments.auth

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_fragment.view.*
import kotlinx.android.synthetic.main.reg_fragment.view.text_email
import kotlinx.android.synthetic.main.reset_pass_email_fragment.view.*
import ru.mendel.apps.rcoko27.*
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.data.DataValidator
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject

class ResetPasswordEmailFragment: AbstractAuthFragment(){

    private lateinit var mObserver : ActionDataObserver

    private fun next(message: ActionData) {
        when (message.actionName) {
            ActionData.ACTION_TO_NEXT -> next()
            ActionData.ACTION_ERROR -> {
                val type = message.data[ActionData.ITEM_TYPE]

                when (type) {
                    "email" -> showMessage(R.string.contains_email)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reset_pass_email_fragment,container,false)

        view.button_next.setOnClickListener { getCode() }

        return view
    }

    override fun onStop() {
        super.onStop()
        mObserver.onComplete()
    }


    private fun getCode(){
        if (!DataValidator.isTrivialString(view!!.text_email.text.toString())){
            if (!DataValidator.isValidEmail(view!!.text_email.text.toString())){
                showMessage(R.string.not_valid_email)
                return
            }
            //отправляем данные на сервер
            APIHelper.sendResetPassword(appname = activity!!.packageName,
                email = view!!.text_email.text.toString())
        }else{
            //выводи сообщение что чего-то нехватает
            showMessage(R.string.need_specify_email)
        }
    }

    private fun next(){
        //код перехода ко второму фрагменту в котором нужно ввести код
        val fragment = ResetPasswordCodeFragment.newInstance(
            view!!.text_email.text.toString()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.sharedElementEnterTransition = DetailsTransition()
            fragment.enterTransition = Fade()
            exitTransition = Fade()
            fragment.sharedElementReturnTransition = DetailsTransition()
        }

        activity!!.supportFragmentManager
            .beginTransaction()
            .addSharedElement(view!!.button_next, "regTransition")//какое view переходит куда
            .replace(R.id.fragment_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

}
package ru.mendel.apps.rcoko27.fragments.auth

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_pass_fragment.view.*
import ru.mendel.apps.rcoko27.DetailsTransition
import ru.mendel.apps.rcoko27.R

class RegPasswordFragment : AbstractAuthFragment() {

    companion object {

        const val EMAIL = "email"
        const val NAME = "name"
        const val CODE = "code"

        fun newInstance(email:String, name:String, code:String): RegPasswordFragment {
            val fragment = RegPasswordFragment()
            val args = Bundle()
            args.putString(EMAIL,email)
            args.putString(NAME,name)
            args.putString(CODE,code)
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var mEmail:String
    private lateinit var mName:String
    private lateinit var mCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEmail = arguments!!.getString(EMAIL)
        mName = arguments!!.getString(NAME)
        mCode = arguments!!.getString(CODE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.reg_pass_fragment,container,false)

        view.button_reg.setOnClickListener { check() }

        return view
    }

    private fun next(){
        //код перехода ко второму фрагменту
        val pass = view!!.text_pass_1.text.toString()
        val fragment = RegRoleFragment.newInstance(
            mEmail,
            mName,
            mCode,
            pass
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
        next()
    }
}
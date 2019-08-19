package ru.mendel.apps.rcoko27.fragments

import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reg_role_fragment.view.*
import kotlinx.android.synthetic.main.settings_fragment.view.*
import kotlinx.android.synthetic.main.settings_fragment.view.chip_group
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class SettingFragment : BaseEventFragment() {

    companion object{

        private const val NAME = "name"
        private const val ROLE = "role"

        fun newInstance(name: String, role: Int):SettingFragment{
            val fragment = SettingFragment()
            val args = Bundle()
            args.putString(NAME, name)
            args.putInt(ROLE, role)
            fragment.arguments = args
            return fragment
        }
    }

    fun next(message: BaseResponse){
        activity?.finish()
    }

    override fun subscribe() {
        val observerSettings = ResponseObserver{x->next(x)}
        ReactiveSubject.addSubscribe(observerSettings, BaseRequest.ACTION_EDIT_SETTINGS)
        mObservers.add(observerSettings)
    }

    override fun onStart() {
        super.onStart()
        subscribe()
    }

    override fun onStop() {
        super.onStop()
        unsubscribe()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_fragment,container, false)

        view.text_user_name.setText(arguments!!.getString(NAME))

        view.chip_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_student -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled = false
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled = true
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled = true

                }
                R.id.chip_parent -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled = true
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled = false
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled = true

                }
                R.id.chip_worker -> {
                    view.chip_group.findViewById<Chip>(R.id.chip_student).isEnabled = true
                    view.chip_group.findViewById<Chip>(R.id.chip_parent).isEnabled = true
                    view.chip_group.findViewById<Chip>(R.id.chip_worker).isEnabled = false
                }
            }
        }

        val role = arguments!!.getInt(ROLE)
        when(role){
            0->view.chip_group.check(R.id.chip_student)
            1->view.chip_group.check(R.id.chip_parent)
            2->view.chip_group.check(R.id.chip_worker)
        }

        view.button_save.setOnClickListener { save() }

        return view
    }

    private fun save(){
        //берем токен и новые данные и отправляем

        val token = QueryPreference.getToken(activity!!)

        val role = when (view!!.chip_group.checkedChipId){
            R.id.chip_student -> 0
            R.id.chip_parent -> 1
            R.id.chip_worker -> 2
            else -> -1
        }

        val name = view!!.text_user_name.text.toString()

        APIHelper.editSettings(appname = activity!!.packageName,
            token = token!!,
            name = name,
            role = role)

    }
}
package ru.mendel.apps.rcoko27.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.activities.SettingsActivity
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.requests.BaseRequest
import ru.mendel.apps.rcoko27.api.responses.BaseResponse
import ru.mendel.apps.rcoko27.api.responses.GetSettingsResponse
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver

class SettingsLoadingFragment : BaseEventFragment() {

    private fun next(message: BaseResponse){
        val data = message as GetSettingsResponse
        //открываем страницу настроек
        val fragment = SettingFragment.newInstance(data.name!!, data.role)
        (activity!! as SettingsActivity).setFragment(fragment)
    }

    override fun subscribe() {
        val observerSettings = ResponseObserver{x->next(x)}
        ReactiveSubject.addSubscribe(observerSettings, BaseRequest.ACTION_GET_SETTINGS)
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
        val view = inflater.inflate(R.layout.settings_load_frgament, container, false)
        //запускаем отсюда запрос на получение данных о пользователе
        val token = QueryPreference.getToken(activity!!)
        APIHelper.getSettings(activity!!.packageName, token!!)
        return view
    }
}
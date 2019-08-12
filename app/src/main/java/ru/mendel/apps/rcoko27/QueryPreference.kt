package ru.mendel.apps.rcoko27

import android.content.Context
import android.preference.PreferenceManager

object QueryPreference {
    private const val PREF_LOGIN = "login"
    private const val PREF_PASSWORD = "password"
    private const val PREF_TOKEN = "token"

    fun getLogin(context: Context): String?{
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LOGIN, null)
    }

    fun setLogin(context: Context, query: String?){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LOGIN, query)
                .apply()
    }

    fun getPassword(context: Context): String?{
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_PASSWORD, null)
    }

    fun setPassword(context: Context, query: String?){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_PASSWORD, query)
            .apply()
    }

    fun getToken(context: Context): String?{
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_TOKEN, null)
    }

    fun setToken(context: Context, query: String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_TOKEN, query)
            .apply()
    }

}
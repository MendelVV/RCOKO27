package ru.mendel.apps.rcoko27.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.fragments.SettingsLoadingFragment

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_activity)

        val fragment = SettingsLoadingFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit()

    }

    fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit()
    }
}
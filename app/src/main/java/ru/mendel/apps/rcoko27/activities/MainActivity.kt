package ru.mendel.apps.rcoko27.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.fragments.EventsListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = EventsListFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_layout, fragment).commit()
    }
}

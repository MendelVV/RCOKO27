package ru.mendel.apps.rcoko27.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.fragments.EventPagerFragment

class EventActivity : AppCompatActivity() {

    companion object {

        const val CODE = "code"

        fun newInstance(context: Context, code: Int): Intent {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(CODE, code)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        val code = intent.getIntExtra(CODE,-1)

        val fragment = EventPagerFragment.newInstance(code)
        supportFragmentManager.beginTransaction().add(R.id.fragment_layout, fragment).commit()
    }

}
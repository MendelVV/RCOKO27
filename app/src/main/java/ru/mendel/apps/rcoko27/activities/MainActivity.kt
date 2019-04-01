package ru.mendel.apps.rcoko27.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.fragments.ActivitiesFragment
import ru.mendel.apps.rcoko27.fragments.EventsListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mItemEvents:MenuItem
    private lateinit var mItemActivities:MenuItem
    private lateinit var mCurrentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setBottomMenu()
        mCurrentFragment = EventsListFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_layout, mCurrentFragment).commit()
    }

    private fun setBottomMenu(){
        val menu = bottom_menu.menu
        mItemEvents = menu.findItem(R.id.action_events)
        mItemEvents.setOnMenuItemClickListener { setEventsFragment() }

        mItemActivities = menu.findItem(R.id.action_activities)
        mItemActivities.setOnMenuItemClickListener { setActivitiesFragment() }
    }

    private fun setEventsFragment(): Boolean {
        Log.d("MyTag","setEvents")
        if (mCurrentFragment is ActivitiesFragment){
            mItemActivities.isChecked = false
            mItemEvents.isChecked = true
            mCurrentFragment = EventsListFragment()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_left,R.anim.out_right)
                .replace(R.id.fragment_layout, mCurrentFragment)
                .commit()
            return true
        }else{

            return false
        }
    }

    private fun setActivitiesFragment(): Boolean {
        Log.d("MyTag","setActivities")
        if (mCurrentFragment is EventsListFragment){
            mItemEvents.isChecked = false
            mItemActivities.isChecked = true
            mCurrentFragment = ActivitiesFragment()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.in_right,R.anim.out_left)
                .replace(R.id.fragment_layout, mCurrentFragment)
                .commit()
            return true
        }else{
            return false
        }

    }
}

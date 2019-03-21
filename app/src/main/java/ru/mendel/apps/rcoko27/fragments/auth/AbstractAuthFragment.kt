package ru.mendel.apps.rcoko27.fragments.auth

import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import ru.mendel.apps.rcoko27.async.BasicAsync

abstract class AbstractAuthFragment : Fragment() {

    lateinit var mTask : BasicAsync

    protected fun showMessage(id: Int){
        Snackbar.make(activity!!.findViewById(android.R.id.content),
            id, Snackbar.LENGTH_LONG).show()
    }

}
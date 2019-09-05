package ru.mendel.apps.rcoko27.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.edit_message_dialog.view.*
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.api.APIHelper

class EditMessageDialog : DialogFragment() {

    companion object{
        const val TEXT = "text"
        const val ID = "uuid"

        fun newInstance(uuid: String, text: String):EditMessageDialog{
            val dialog = EditMessageDialog()
            val args = Bundle()
            args.putString(ID, uuid)
            args.putString(TEXT, text)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity!!).inflate(R.layout.edit_message_dialog,null)
        val uuid = arguments!!.getString(ID)
        val text = arguments!!.getString(TEXT)
        view.edit_message_text.setText(text)

        return AlertDialog.Builder(activity!!)
            .setView(view)
            .setTitle(R.string.edit_text)
            .setPositiveButton(R.string.edit) { dialogInterface: DialogInterface, i: Int ->
                APIHelper.editMessage(appname = activity!!.packageName,
                    token = QueryPreference.getToken(activity!!)!!,
                    uuid = uuid!!,
                    text = view.edit_message_text.text.toString())
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.cancel){ dialogInterface: DialogInterface, i:Int ->
                dialogInterface.dismiss()
            }
            .create()
    }
}
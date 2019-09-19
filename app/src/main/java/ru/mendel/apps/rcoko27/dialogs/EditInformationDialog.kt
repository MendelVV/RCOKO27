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

class EditInformationDialog : DialogFragment() {

    companion object{
        const val TEXT = "text"
        const val ID = "code"

        fun newInstance(code: Int, text: String):EditInformationDialog{
            val dialog = EditInformationDialog()
            val args = Bundle()
            args.putInt(ID, code)
            args.putString(TEXT, text)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity!!).inflate(R.layout.edit_message_dialog,null)
        val code = arguments!!.getInt(ID)
        val text = arguments!!.getString(TEXT)
        view.edit_message_text.setText(text)

        return AlertDialog.Builder(activity!!)
            .setView(view)
            .setTitle(R.string.edit_text)
            .setPositiveButton(R.string.edit) { dialogInterface: DialogInterface, i: Int ->
                APIHelper.editInformation(appname = activity!!.packageName,
                    token = QueryPreference.getToken(activity!!)!!,
                    code = code,
                    text = view.edit_message_text.text.toString())
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.cancel){ dialogInterface: DialogInterface, i:Int ->
                dialogInterface.dismiss()
            }
            .create()
    }
}
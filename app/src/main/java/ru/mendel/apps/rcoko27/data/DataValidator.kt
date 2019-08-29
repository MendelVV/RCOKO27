package ru.mendel.apps.rcoko27.data

import java.util.regex.Pattern

object DataValidator {

    fun isValidEmail(email: String): Boolean{

        val emailRegex = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        return emailRegex.matcher(email).matches()
    }

    fun isTrivialString(s: String):Boolean{
        return clearString(s).isEmpty()
    }

    private fun clearString(s: String): String{
        var res = s.replace(" ","")
        res = res.replace("\n","")
        res = res.replace("","")
        return res
    }
}
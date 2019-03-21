package ru.mendel.apps.rcoko27.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class PossibleData() {
    var code = -1
    var voting = -1
    var text : String? = null

    constructor(jsonObject: JSONObject) : this() {
        try {
            code = jsonObject.getInt("code")
            voting = jsonObject.getInt("voting")
            text = jsonObject.getString("text")
        }catch (e: JSONException){
            Log.e("MyTag",e.toString())
        }
    }

    override fun toString(): String {
        return "code:$code, voting:$voting, text:$text"
    }
}
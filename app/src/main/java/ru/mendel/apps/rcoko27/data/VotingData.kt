package ru.mendel.apps.rcoko27.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class VotingData() {
    var code = -1
    var event = -1
    var text : String? = null
    var possibles: MutableList<PossibleData> = mutableListOf()
    var answers: MutableList<AnswerData> = mutableListOf()

    constructor(jsonObject: JSONObject) : this() {
        try {
            code = jsonObject.getInt("code")
            event = jsonObject.getInt("event")
            text = jsonObject.getString("text")
            val arr = jsonObject.getJSONArray("possibles")
            val n = arr.length()
            for (i in 0 until n){
                possibles.add(PossibleData(arr.getJSONObject(i)))
            }
        }catch (e: JSONException){
            Log.e("MyTag",e.toString())
        }
    }

    fun totalAnswers():Int{
        var sum = 0
        for (answer in answers){
            sum+=answer.size
        }
        return sum
    }

    override fun toString(): String {
        var res = "code: $code, event:$event, text:$text"
        res+="["
        for (possible in possibles){
            res+= "{$possible}"
        }
        res+="]"
        return res
    }
}
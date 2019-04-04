package ru.mendel.apps.rcoko27.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.mendel.apps.rcoko27.api.RcokoClient

class EventData() {

    companion object {
        fun convertDate(date: String): String{
            val a = date.substring(0,10)
                                    .split("-")
            if (a.size==3){
                val res = a[2]+"."+a[1]+"."+a[0]
                return res
            }else{
                return date
            }
        }
    }

    //класс данных
    var code : Int = 0
    var title : String? = null
    var text : String? = null
    var dateevent : String? = null
    var datenews : String? = null
    var type : String? = null
    var icon : String? = null
    var state : Int = -1
    var votingcount: Int = 0
    var messagescount: Int = 0

    constructor(jsonObject: JSONObject) : this() {
        try {
            code = jsonObject.getInt("code")
            title = jsonObject.getString("title")
            text = jsonObject.getString("text")
            dateevent = jsonObject.getString("date_event")
            datenews = jsonObject.getString("date_news")
            type = jsonObject.getString("type")
            icon = jsonObject.getString("icon")
            state = jsonObject.getInt("state")
            votingcount = jsonObject.getInt("voting_count")
            messagescount = jsonObject.getInt("messages_count")
        }catch (e:JSONException){
            Log.e("MyTag",e.toString())
        }

    }

    fun getImageUrl(): String?{
        if (icon!="false"){
            return RcokoClient.IMAGE_URL+"event_"+ code + icon
        }
        return null
    }

}

package ru.mendel.apps.rcoko27.data

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.mendel.apps.rcoko27.json.JsonSchema
import java.text.SimpleDateFormat
import java.util.*

class MessageData() {

    companion object {
        const val STATE_NONE = -1
        const val STATE_SEND = 0
        const val STATE_DELIVERED = 1

        fun convertData(date: String, gmt:String):String{
            //пришли данные в виде 2019-12-10 10:10:10, +02:00
            val calendar = GregorianCalendar()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
            calendar.time = format.parse("$date $gmt")
//            System.out.println(calendar.toString())

            val formatCurrent = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
/*            val dateF = SimpleDateFormat("Z", Locale.getDefault())
            val localTime = dateF.format(System.currentTimeMillis())
            System.out.println("time = $localTime")
            //нужно узнать местный часовой пояс и соответственно разницу во времени*/

            return formatCurrent.format(calendar.time)
        }

    }

    var code : Int = 0
    var event : Int = 0
    var date: String? = null
    var author: String? = null
    var authorname: String? = null
    var recipient: String? = null
    var recipientname: String? = null
    var text: String? = null
    var uuid: String? = null
    var state: Int = -1
    var gmt: String? = null

    constructor(jsonObject: JSONObject):this(){
        try {
            code = jsonObject.getInt(JsonSchema.SendMessage.CODE)
            event = jsonObject.getInt(JsonSchema.SendMessage.EVENT)
            date = jsonObject.getString(JsonSchema.SendMessage.DATE)
            author = jsonObject.getString(JsonSchema.SendMessage.AUTHOR)
            authorname = jsonObject.getString(JsonSchema.SendMessage.AUTHOR_NAME)
            recipient = jsonObject.getString(JsonSchema.SendMessage.RECIPIENT)
            recipientname = jsonObject.getString(JsonSchema.SendMessage.RECIPIENT_NAME)
            text = jsonObject.getString(JsonSchema.SendMessage.TEXT)
            uuid = jsonObject.getString(JsonSchema.SendMessage.UUID)
            state = STATE_DELIVERED//получено с сервера хначит доставлено на сервер
        }catch (e: JSONException){
            Log.e("MyTag",e.toString())
        }

    }

    fun toJson():JSONObject{
        //только данные необходимые для отправки бех имени пакета, пароля и действия
        val jsonObject = JSONObject()
        jsonObject.put(JsonSchema.SendMessage.AUTHOR, author)
        jsonObject.put(JsonSchema.SendMessage.RECIPIENT, recipient)
        jsonObject.put(JsonSchema.SendMessage.TEXT, text)
        jsonObject.put(JsonSchema.SendMessage.EVENT, event)
        jsonObject.put(JsonSchema.SendMessage.UUID, uuid)

        return jsonObject
    }
}
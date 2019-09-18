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

        fun convertDate(date: String, gmt:String):String{
            //пришли данные в виде 2019-12-10 10:10:10, +02:00
            val calendar = GregorianCalendar()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")
            calendar.time = format.parse("$date $gmt")
            val formatCurrent = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
            //нужно узнать местный часовой пояс и соответственно разницу во времени*/

            return formatCurrent.format(calendar.time)
        }

    }

    var code : Int = 0
    var event : Int = 0
    var date: String? = null
    var author: String? = null
    var authorname: String? = null
    var parentmessage: Int=-1
    var text: String? = null
    var uuid: String? = null
    var state: Int = -1
    var gmt: String? = null
    var verification: Int = 0//ставить ли галочку при выводе

}
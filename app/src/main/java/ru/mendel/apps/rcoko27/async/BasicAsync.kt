package ru.mendel.apps.rcoko27.async

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import ru.mendel.apps.rcoko27.json.JsonSchema
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection

open class BasicAsync(val url_str: String) : AsyncTask<JSONObject, Void, JSONObject>(){

    override fun doInBackground(vararg args: JSONObject?): JSONObject? {
        val jsonObject = args[0]
        val url = java.net.URL(url_str)

        try{
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 7000//8 секунд на ожидание ответа
            connection.readTimeout = 10000//10 секунд на чтение
            connection.requestMethod = "POST"
            connection.useCaches = false
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connect()
            val os = connection.outputStream

            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(jsonObject.toString())
            writer.close()
            os.close()

            val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
            val sb = StringBuilder()
            var line: String?
            line = reader.readLine()
            while (line!=null) {
                sb.append(line)
                line = reader.readLine()
            }
            Log.d("MyTag", "response = $sb")
            val result = JSONObject(sb.toString())

            return result
        }catch (e:ConnectException){
            val result = JSONObject()
            result.put(JsonSchema.Response.RESULT,"error")
            result.put(JsonSchema.Response.TYPE,"network")
            return result
        }catch (e: JSONException){
            val result = JSONObject()
            result.put(JsonSchema.Response.RESULT,"error")
            result.put(JsonSchema.Response.TYPE,"json")
            return result
        }

    }

}
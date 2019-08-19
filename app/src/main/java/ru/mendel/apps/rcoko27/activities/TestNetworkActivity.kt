package ru.mendel.apps.rcoko27.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.test_network_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.api.requests.AutoLoginRequest
import ru.mendel.apps.rcoko27.api.requests.RegRequest
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class TestNetworkActivity : AppCompatActivity() {

    val mUiThread = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_network_layout)

        button_send.setOnClickListener { GlobalScope.launch(Dispatchers.IO) { send() }  }
    }


    private fun send(){

        try{
            val TOKEN = "0ffe9501-d346-45e6-b77e-e24a43ddec47"
            val urlString = "http://feedback.rcoko27.ru/feedback/api/registration.php"
            val dataReg = AutoLoginRequest()
            dataReg.email = "mendel_ww@mail.ru"
            dataReg.password = "77uter24"
            dataReg.action = "auth"
            dataReg.appname = packageName
            val data = Gson().toJson(dataReg)
            val res = request(urlString, data, TOKEN)
            mUiThread.post { view_response.text = res }
        }catch (e: Exception){
            mUiThread.post {
                var info = e.message
                for (s in e.stackTrace){
                    info+="{${s.fileName} ${s.className} ${s.methodName} ${s.lineNumber} }\n"
                }
                view_response.text = info
            }
        }

    }

    fun request(urlString: String, data: String, token:String):String{
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 7000//8 секунд на ожидание ответа
        connection.readTimeout = 10000//10 секунд на чтение
        connection.requestMethod = "POST"
        connection.useCaches = false
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("token",token)
        connection.connect()
        val os = connection.outputStream

        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8") as Writer)
        writer.write(data)
        writer.close()
        os.close()

        val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
        val sb = StringBuilder()
        var line = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        return sb.toString()
    }
}
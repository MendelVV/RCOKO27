package ru.mendel.apps.rcoko27

import android.net.Uri
import android.util.Patterns
import org.junit.Test
import ru.mendel.apps.rcoko27.data.DataValidator
import java.util.regex.Pattern

class DataTests {

    @Test
    fun testEmailValid(){

        val email1 = "mendel_ww@mail.ru"
        assert(DataValidator.isValidEmail(email1))
        val email2 = "mendel.vasilij@gmail.com"
        assert(DataValidator.isValidEmail(email2))
        val email3 = "mendel.vasilij@gmail"
        assert(!DataValidator.isValidEmail(email3))
    }

    @Test
    fun testTrivialString(){
        val s1 = "     "
        assert(DataValidator.isTrivialString(s1))
        val s2 = "     sd"
        assert(!DataValidator.isTrivialString(s2))
    }

    @Test
    fun searchURL(){
 //       val URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"

//        val p = Pattern.compile(URL_REGEX)
        val text = "This is a new message. The content of the message is in 'https://www.example.com/asd/abc' "
       // val links = ArrayList<String>()
        val m = Patterns.WEB_URL.matcher(text)
        while (m.find()) {
            val url = m.group()
            println(url)
         }


    }
}
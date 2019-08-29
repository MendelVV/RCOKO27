package ru.mendel.apps.rcoko27

import org.junit.Test
import ru.mendel.apps.rcoko27.data.DataValidator

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
}
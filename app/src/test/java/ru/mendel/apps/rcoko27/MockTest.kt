package ru.mendel.apps.rcoko27

import android.util.Log
import org.junit.Test
import org.mockito.Mockito.*
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter


class MockTest {
    @Test
    fun iterator_will_return_hello_world() {
        //подготавливаем
        val i = mock(Log::class.java)
        `when`(i.toString()).thenReturn("Hello").thenReturn("World")
        //выполняем
        val result = i.toString() + " " + i.toString()
        //сравниваем
        assert(result=="Hello World")
    }


    @Test(expected = IOException::class)
//    @Throws(IOException::class)
    fun streamTest() {
        val mock = mock(OutputStream::class.java)
        val osw = OutputStreamWriter(mock)
//        doThrow(IOException()).`when`(mock).close()
        doReturn("Test").`when`(mock).close()
        osw.close()
    }
}
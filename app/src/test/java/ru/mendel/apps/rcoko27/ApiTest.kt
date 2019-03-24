package ru.mendel.apps.rcoko27

import kotlinx.android.synthetic.main.auth_fragment.view.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.AutoLoginRequest
import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject

class ApiTest {

    @Before
    fun initTest() {
        //срабатывает перед каждым тестом
        System.out.println("before")
    }

    @After
    fun afterTest() {
        System.out.println("after")
    }

    @Test(timeout = 7000)
    fun autoAuth(){

        var b = true
        val observer = ActionDataObserver{
                x->
            assert(x.actionName==ActionData.ACTION_TO_MAIN)
            System.out.println("end")
            b=false
        }
        ReactiveSubject.addSubscribe(observer)
        APIHelper.sendAutoLogin(appname = "ru.mendel.apps.rcoko27",
            email = "mendel_ww@mail.ru",
            password = "77uter24")
        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
    }


}
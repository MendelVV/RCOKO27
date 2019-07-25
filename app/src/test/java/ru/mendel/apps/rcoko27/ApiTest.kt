package ru.mendel.apps.rcoko27

import kotlinx.android.synthetic.main.auth_fragment.view.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.mendel.apps.rcoko27.api.APIHelper
import ru.mendel.apps.rcoko27.api.RcokoClient
import ru.mendel.apps.rcoko27.api.requests.GetDataRequest
import ru.mendel.apps.rcoko27.api.responses.ActivitiesResponse
import ru.mendel.apps.rcoko27.api.responses.GetDataResponse
import ru.mendel.apps.rcoko27.api.responses.GetEventResponse
import ru.mendel.apps.rcoko27.api.responses.UpdateMessagesResponse

import ru.mendel.apps.rcoko27.data.ActionData
import ru.mendel.apps.rcoko27.reactive.ActionDataObserver
import ru.mendel.apps.rcoko27.reactive.ReactiveSubject
import ru.mendel.apps.rcoko27.reactive.ResponseObserver
import java.util.*

class ApiTest {

    companion object {
        const val APP_NAME = "ru.mendel.apps.rcoko27"
        const val LOGIN = "mendel_ww@mail.ru"
        const val PASSWORD = "77uter24"
    }

    @Before
    fun initTest() {
        //срабатывает перед каждым тестом
        System.out.println("before")
    }

    @After
    fun afterTest() {
        println("after")
    }

    @Test(timeout = 7000)
    fun autoAuth(){

        var b = true
        val observer = ActionDataObserver{
                x->
            assert(x.actionName==ActionData.ACTION_TO_MAIN)
            println("end")
            b=false
        }
        val token = UUID.randomUUID().toString()
        ReactiveSubject.addSubscribe(observer)
        APIHelper.sendAutoLogin(appname = APP_NAME,
            email = LOGIN,
            password = PASSWORD,
            token = token)
        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
        observer.onComplete()
    }

    @Test(timeout = 7000)
    fun getEvents(){
        var b = true
        val observer = ResponseObserver{x->
            assert(x.result=="ok")
            val response = x as GetDataResponse
            assert(response.data.size==5)
            b=false
        }

        ReactiveSubject.addSubscribe(observer)

        APIHelper.getEvents(appname = APP_NAME,
            email = LOGIN,
            password = PASSWORD,
            start = 0,
            size = 5)

        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
        observer.onComplete()
    }

    @Test(timeout = 7000)
    fun getEvent() {
        var b = true
        val observer = ResponseObserver{x->
            assert(x.result=="ok")
            val response = x as GetEventResponse
            assert(response.event!!.code==28)
            println(response.event!!.icon)
            b=false
        }
        ReactiveSubject.addSubscribe(observer)

        APIHelper.getEvent(appname = APP_NAME,
            email = LOGIN,
            password = PASSWORD,
            code = 28)


        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
        observer.onComplete()
    }

    @Test(timeout = 7000)
    fun updateMessages() {
        var b = true
        val observer = ResponseObserver{x->
            assert(x.result=="ok")
            val response = x as UpdateMessagesResponse
            assert(response.messages.size>0)
            b=false
        }
        ReactiveSubject.addSubscribe(observer)

        APIHelper.updateMessages(appname = APP_NAME,
            email = LOGIN,
            password = PASSWORD,
            event = 1)

        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
        observer.onComplete()
    }


    @Test(timeout = 7000)
    fun getActivities() {
        var b = true
        val observer = ResponseObserver{x->
            assert(x.result=="ok")
            val response = x as ActivitiesResponse
            for (data in response.activities){
                System.out.println(data.toString())
            }
            System.out.println("events.size=${response.events.size} voting.size=${response.votings.size}")
//            assert(response.messages.size>0)
            b=false
        }
        ReactiveSubject.addSubscribe(observer)

        APIHelper.getActivities(appname = APP_NAME,
            email = LOGIN,
            password = PASSWORD)

        while (b){
            Thread.sleep(100)
            //тут просто ждем пока все не закончится
        }
        observer.onComplete()
    }

}
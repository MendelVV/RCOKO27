package ru.mendel.apps.rcoko27.api

import kotlinx.android.synthetic.main.reg_fragment.view.*
import ru.mendel.apps.rcoko27.api.requests.*
import ru.mendel.apps.rcoko27.fragments.EventsListFragment

object APIHelper {

    const val ACTION_AUTH = "auth"
    const val ACTION_REG = "reg"
    const val ACTION_VERIFY_CODE = "verify_code"
    const val ACTION_REGISTRATION = "registration"

    const val ACTION_GET_EVENTS = "get_events"
    const val ACTION_REFRESH_EVENTS = "refresh_events"
    const val ACTION_UPDATE_EVENTS = "update_events"

    const val ACTION_GET_EVENT = "get_event"

    const val ACTION_SEND_MESSAGE = "send_message"
    const val ACTION_UPDATE_MESSAGES = "update_messages"

    const val ACTION_VOTE = "vote"

    const val ACTION_GET_ACTIVITIES = "get_activities"

    fun sendAutoLogin(appname:String, email:String,password:String){
        val request = AutoLoginRequest()
        request.appname = appname
        request.action = ACTION_AUTH
        request.email = email
        request.password = password
        RcokoClient.autoLogin(request)
    }

    fun sendReg(appname:String, email:String,name:String){
        val request = RegRequest()
        request.appname = appname
        request.action = ACTION_REG
        request.email = email
        request.name = name
        RcokoClient.reg(request)
    }

    fun sendRegCode(appname:String, email:String,code:String){
        val request = RegCodeRequest()
        request.appname = appname
        request.action = ACTION_VERIFY_CODE
        request.email = email
        request.code = code

        RcokoClient.regCode(request)
    }

    fun sendRegistration(appname:String, email:String, name:String, code:String, password:String, role:Int){
        val request = RegistrationRequest()
        request.appname = appname
        request.action = ACTION_REGISTRATION
        request.email = email
        request.name = name
        request.code = code
        request.password = password
        request.role = role

        RcokoClient.registration(request)
    }

    fun refreshEvents(appname:String, email:String, password:String, start:Int, size: Int){
        val request = GetDataRequest()
        request.appname = appname
        request.action = ACTION_REFRESH_EVENTS
        request.email = email
        request.password = password
        request.start = start
        request.size = size

        RcokoClient.getData(request)
    }

    fun getEvents(appname:String, email:String, password:String, start:Int, size: Int){
        val request = GetDataRequest()
        request.appname = appname
        request.action = ACTION_GET_EVENTS
        request.email = email
        request.password = password
        request.start = start
        request.size = size

        RcokoClient.getData(request)
    }

    fun updateEvents(appname:String, email:String, password:String, start:String, end: String){
        val request = UpdateEventsRequest()
        request.appname = appname
        request.action = ACTION_UPDATE_EVENTS
        request.email = email
        request.password = password
        request.start = start//дата первого события
        request.end = end//дата последнего события

        RcokoClient.updateEvents(request)
    }

    fun getEvent(appname:String, email:String, password:String, code:Int){
        val request = GetEventRequest()
        request.appname = appname
        request.action = ACTION_GET_EVENT
        request.email = email
        request.password = password
        request.code = code

        RcokoClient.getEvent(request)
    }

    fun sendMessage(appname:String, password:String, author:String, parentmessage:Int,text:String,event:Int, uuid:String){
        val request = SendMessageRequest()
        request.appname = appname
        request.action = ACTION_SEND_MESSAGE
        request.password = password

        request.author = author
        request.parentmessage = parentmessage
        request.text = text
        request.event = event
        request.uuid = uuid

        RcokoClient.sendMessage(request)
    }

    fun updateMessages(appname:String, email:String, password:String, event:Int){
        val request = UpdateMessagesRequest()

        request.appname = appname
        request.action = ACTION_UPDATE_MESSAGES
        request.password = password
        request.email = email
        request.event = event

        RcokoClient.updateMessages(request)
    }

    fun vote(appname:String, email:String, password:String, voting:Int,answer:Int){
        val request = VoteRequest()
        request.appname = appname
        request.action = ACTION_VOTE
        request.password = password
        request.email = email
        request.voting = voting
        request.answer = answer

        RcokoClient.vote(request)
    }

    fun getActivities(appname:String, email:String, password:String){
        val request = ActivitiesRequest()
        request.appname = appname
        request.action = ACTION_GET_ACTIVITIES
        request.password = password
        request.email = email

        RcokoClient.getActivities(request)
    }
}
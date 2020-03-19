package ru.mendel.apps.rcoko27.api

import android.util.Log
import ru.mendel.apps.rcoko27.api.requests.*

object APIHelper {

    fun sendAutoLogin(appname:String, email:String, password:String, token: String){
        val request = AutoLoginRequest(email=email,
            password = password)
        request.appname = appname
        RcokoClient.autoLogin(request, token)
    }

    fun sendReg(appname:String, email:String, name:String){
        val request = RegRequest(email = email,
            name = name)
        request.appname = appname
        RcokoClient.reg(request)
    }

    fun sendRegCode(appname:String, email:String,code:String){
        val request = RegCodeRequest(email = email, code = code)
        request.appname = appname
        RcokoClient.regCode(request)
    }

    fun sendResetPassword(appname:String, email:String){
        val request = ResetPasswordRequest(email = email)
        request.appname = appname
        RcokoClient.resetPassword(request)
    }

    fun newPassword(appname: String, token: String, email: String, code: String, password: String){
        val request = NewPasswordRequest(email = email,
            code = code,
            password = password)
        request.appname = appname
        RcokoClient.newPassword(request, token)
    }

    fun sendRegistration(appname:String, token: String, email:String, name:String, code:String, password:String, role:Int){
        val request = RegistrationRequest(email = email,
            name = name,
            code = code,
            password = password,
            role = role)
        request.appname = appname
        RcokoClient.registration(request, token)
    }

    fun refreshEvents(appname:String, token: String, start:Int, size: Int){
        val request = GetDataRequest(start = start, size = size, action = BaseRequest.ACTION_REFRESH_EVENTS)
        request.appname = appname
        RcokoClient.getData(request, token)
    }

    fun getEvents(appname:String, token:String, start:Int, size: Int){
        val request = GetDataRequest(start = start, size = size, action = BaseRequest.ACTION_GET_EVENTS)
        request.appname = appname
        RcokoClient.getData(request, token)
    }

    fun updateEvents(appname:String, token:String, start:String, end: String){
        val request = UpdateEventsRequest(start = start, end = end)
        request.appname = appname
        RcokoClient.updateEvents(request, token)
    }

    fun getEvent(appname:String, token:String, code:Int){
        val request = GetEventRequest(code = code)
        request.appname = appname
        RcokoClient.getEvent(request, token)
    }

    fun sendMessage(appname:String, token:String, parentmessage:Int,text:String,event:Int, uuid:String){
        val request = SendMessageRequest(parentmessage = parentmessage,
            text = text,
            event = event,
            uuid = uuid)
        request.appname = appname
        RcokoClient.sendMessage(request, token)
    }

    fun updateMessages(appname:String, token:String, event:Int){
        val request = UpdateMessagesRequest(event = event)
        request.appname = appname
        RcokoClient.updateMessages(request, token)
    }

    fun vote(appname:String, token:String, voting:Int,answer:Int){
        val request = VoteRequest(voting = voting, answer = answer)
        request.appname = appname
        RcokoClient.vote(request, token)
    }

    fun getActivities(appname:String, token:String){
        val request = ActivitiesRequest()
        request.appname = appname
        RcokoClient.getActivities(request, token)
    }

    fun getSettings(appname:String, token:String){
        val request = GetSettingsRequest()
        request.appname = appname
        RcokoClient.getSettings(request, token)
    }

    fun editSettings(appname:String, token:String, name: String, role: Int){
        val request = EditSettingsRequest(name = name, role = role)
        request.appname = appname
        RcokoClient.editSettings(request, token)
    }

    fun removeMessage(appname:String, token:String, uuid: String){
        val request = RemoveMessageRequest(uuid = uuid)
        request.appname = appname
        RcokoClient.removeMessage(request, token)
    }

    fun editMessage(appname:String, token:String, uuid: String, text: String){
       // Log.d("MyTag","editMessage")
        val request = EditMessageRequest(uuid = uuid,
            text = text)
        request.appname = appname
        RcokoClient.editMessage(request, token)
    }

    fun removeAlienMessage(appname:String, token:String, uuid: String, login: String){
        val request = RemoveAlienMessageRequest(uuid = uuid, login = login)
        request.appname = appname
        RcokoClient.removeAlienMessage(request, token)
    }

    fun getInformation(appname:String, token:String, event: Int){
        val request = GetInformationRequest(event = event)
        request.appname = appname
        RcokoClient.getInformation(request, token)
    }

    fun sendInformation(appname:String, token:String, event:Int, text:String){
        val request = SendInformationRequest(event = event,
            text = text)
        request.appname = appname
        RcokoClient.sendInformation(request, token)
    }

    fun removeInformation(appname:String, token:String, code:Int){
        val request = RemoveInformationRequest(code = code)
        request.appname = appname
        RcokoClient.removeInformation(request, token)
    }

    fun editInformation(appname:String, token:String, code:Int, text: String){
        val request = EditInformationRequest(code = code, text = text)
        request.appname = appname
        RcokoClient.editInformation(request, token)
    }

    fun getPushData(appname:String, token:String, date:String){
        val request = GetPushDataRequest(date = date)
        request.appname = appname
        RcokoClient.getPushData(request, token)
    }

}
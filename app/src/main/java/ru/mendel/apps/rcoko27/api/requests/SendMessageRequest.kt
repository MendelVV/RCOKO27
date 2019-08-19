package ru.mendel.apps.rcoko27.api.requests

class SendMessageRequest(var event : Int = 0,
                         var parentmessage: Int=-1,
                         var text: String? = null,
                         var uuid: String? = null) : BaseRequest(action = ACTION_SEND_MESSAGE) {



}
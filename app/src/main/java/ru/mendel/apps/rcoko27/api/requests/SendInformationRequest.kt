package ru.mendel.apps.rcoko27.api.requests

class SendInformationRequest(var event : Int = 0,
                             var text: String? = null) : BaseRequest(action = ACTION_SEND_INFORMATION) {



}
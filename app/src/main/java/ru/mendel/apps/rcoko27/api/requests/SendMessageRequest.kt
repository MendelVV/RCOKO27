package ru.mendel.apps.rcoko27.api.requests

class SendMessageRequest : BaseRequest() {

    var event : Int = 0
    var parentmessage: Int=-1
    var text: String? = null
    var uuid: String? = null

}
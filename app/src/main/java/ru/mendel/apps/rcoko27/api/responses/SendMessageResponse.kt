package ru.mendel.apps.rcoko27.api.responses

class SendMessageResponse : BaseResponse() {

    var event: Int = 0
    var text: String? = null
    var uuid: String? = null
    var date: String? = null
    var code : Int = 0

}
package ru.mendel.apps.rcoko27.api.requests

class SendMessageRequest : BaseRequest() {

    var event : Int = 0
    var author: String? = null
    var recipient: String? = null
    var text: String? = null
    var uuid: String? = null

    var password: String? = null

}
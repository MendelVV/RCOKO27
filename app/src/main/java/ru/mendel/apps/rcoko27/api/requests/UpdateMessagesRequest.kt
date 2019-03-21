package ru.mendel.apps.rcoko27.api.requests

class UpdateMessagesRequest : BaseRequest() {

    var email: String? = null
    var password: String? = null

    var event: Int = 0

}
package ru.mendel.apps.rcoko27.api.requests

class UpdateEventsRequest : BaseRequest() {

    var email: String? = null
    var password: String? = null

    var start : String? = null
    var end : String? = null

}
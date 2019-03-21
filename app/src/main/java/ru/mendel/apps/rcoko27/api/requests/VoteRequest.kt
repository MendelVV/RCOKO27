package ru.mendel.apps.rcoko27.api.requests

class VoteRequest : BaseRequest() {

    var email: String? = null
    var password: String? = null

    var voting : Int = 0
    var answer : Int = 0

}
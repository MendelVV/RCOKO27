package ru.mendel.apps.rcoko27.api.requests

class GetDataRequest :BaseRequest(){
    var email: String? = null
    var password: String? = null
    var start: Int = 0
    var size: Int = 0

}
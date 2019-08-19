package ru.mendel.apps.rcoko27.api.requests

class GetDataRequest(var start: Int = 0,
                     var size: Int = 0,
                     action: String? = null) :BaseRequest(action = action){


}
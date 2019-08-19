package ru.mendel.apps.rcoko27.api.requests

class RegCodeRequest(var email: String? = null,
                     var code: String? = null) : BaseRequest(action = ACTION_VERIFY_CODE){

}
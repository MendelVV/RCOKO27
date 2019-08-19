package ru.mendel.apps.rcoko27.api.requests

class AutoLoginRequest(var email: String? = null,
                       var password: String? = null) : BaseRequest(action = ACTION_AUTH){




}
package ru.mendel.apps.rcoko27.api.requests

class NewPasswordRequest(var email: String? = null,
                         var code: String? = null,
                         var password: String? = null) : BaseRequest(action = ACTION_NEW_PASSWORD) {

}
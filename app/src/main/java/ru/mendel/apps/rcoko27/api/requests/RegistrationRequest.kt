package ru.mendel.apps.rcoko27.api.requests

class RegistrationRequest(var email: String? = null,
                          var name: String? = null,
                          var code: String? = null,
                          var password: String? = null,
                          var role: Int = -1) : BaseRequest(action = ACTION_REGISTRATION) {

}
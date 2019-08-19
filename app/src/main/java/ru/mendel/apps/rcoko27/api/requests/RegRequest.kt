package ru.mendel.apps.rcoko27.api.requests

class RegRequest(var email: String? = null,
                 var name: String? = null) : BaseRequest(action = ACTION_REG) {


}
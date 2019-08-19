package ru.mendel.apps.rcoko27.api.requests

class ResetPasswordRequest(var email: String? = null) : BaseRequest(action = ACTION_RESET_PASSWORD) {


}
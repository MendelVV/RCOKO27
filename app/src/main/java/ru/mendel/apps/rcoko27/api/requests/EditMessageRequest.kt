package ru.mendel.apps.rcoko27.api.requests

class EditMessageRequest(var uuid: String? = null,
                         var text: String? = null) : BaseRequest(action = ACTION_EDIT_MESSAGE) {
}
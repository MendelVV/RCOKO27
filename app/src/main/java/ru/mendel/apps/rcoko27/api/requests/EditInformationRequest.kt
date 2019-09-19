package ru.mendel.apps.rcoko27.api.requests

class EditInformationRequest(var code: Int = -1,
                             var text: String? = null) : BaseRequest(action = ACTION_EDIT_INFORMATION) {
}
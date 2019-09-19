package ru.mendel.apps.rcoko27.api.requests

class RemoveInformationRequest(var code: Int = -1) : BaseRequest(action = ACTION_REMOVE_INFORMATION) {
}
package ru.mendel.apps.rcoko27.api.requests

class RemoveMessageRequest(var uuid: String? = null) : BaseRequest(action = ACTION_REMOVE_MESSAGE) {
}
package ru.mendel.apps.rcoko27.api.requests

class UpdateMessagesRequest(var event: Int = 0) : BaseRequest(action = ACTION_UPDATE_MESSAGES) {
}
package ru.mendel.apps.rcoko27.api.requests

class RemoveAlienMessageRequest(var uuid: String? = null,
                                var login: String? = null) : BaseRequest(action = ACTION_REMOVE_ALIEN_MESSAGE) {
}
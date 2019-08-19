package ru.mendel.apps.rcoko27.api.requests

class UpdateEventsRequest(var start : String? = null,
                          var end : String? = null) : BaseRequest(action = ACTION_UPDATE_EVENTS) {



}
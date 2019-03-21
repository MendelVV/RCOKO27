package ru.mendel.apps.rcoko27.api.responses

class UpdateEventsResponse : BaseResponse() {

    var events = arrayListOf<EventInfo>()

    inner class EventInfo{
        var code = 0
        var messagescount = 0
        var votingcount = 0
    }
}
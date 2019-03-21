package ru.mendel.apps.rcoko27.api.responses

import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.data.VotingData

class GetEventResponse : BaseResponse() {

//    var data = arrayListOf<EventData>()
    var event: EventData? = null
    var messages = arrayListOf<MessageData>()
    var voting = arrayListOf<VotingData>()
}
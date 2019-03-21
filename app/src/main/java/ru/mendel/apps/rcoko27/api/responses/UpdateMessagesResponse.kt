package ru.mendel.apps.rcoko27.api.responses

import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData
import ru.mendel.apps.rcoko27.data.VotingData

class UpdateMessagesResponse : BaseResponse() {

    var messages = arrayListOf<MessageData>()

}
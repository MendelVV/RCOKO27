package ru.mendel.apps.rcoko27.api.responses

import ru.mendel.apps.rcoko27.data.AnswerData

class VoteResponse : BaseResponse() {

    var voting: Int = 0
    var answers: MutableList<AnswerData> = mutableListOf()

}
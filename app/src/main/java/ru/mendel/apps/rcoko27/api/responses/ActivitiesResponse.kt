package ru.mendel.apps.rcoko27.api.responses

import ru.mendel.apps.rcoko27.data.ActivitiesData
import ru.mendel.apps.rcoko27.data.AnswerData
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.VotingData

class ActivitiesResponse : BaseResponse() {

    var activities: MutableList<ActivitiesData> = mutableListOf()
    var events: MutableList<EventData> = mutableListOf()
    var votings: MutableList<VotingData> = mutableListOf()

}
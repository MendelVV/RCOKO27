package ru.mendel.apps.rcoko27.data

class ActivitiesData {

    companion object {
        const val VOTE = "voting"
        const val MESSAGES = "messages"
    }

    var event=0
    var date:String? = null
    var number=0
    var type:String?=null

    override fun toString(): String {
        return "event=$event date=$date number=$number type=$type"
    }
}
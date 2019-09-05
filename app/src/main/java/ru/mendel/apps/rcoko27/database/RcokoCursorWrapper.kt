package ru.mendel.apps.rcoko27.database

import android.database.Cursor
import android.database.CursorWrapper
import ru.mendel.apps.rcoko27.data.*
import ru.mendel.apps.rcoko27.database.RcokoDbSchema.*

class RcokoCursorWrapper (cursor: Cursor): CursorWrapper(cursor) {

    fun getEventData(): EventData {

        val code = getInt(getColumnIndex(TableEvents.Cols.CODE))
        val title = getString(getColumnIndex(TableEvents.Cols.TITLE))
        val text = getString(getColumnIndex(TableEvents.Cols.TEXT))
        val dateEvent = getString(getColumnIndex(TableEvents.Cols.DATE_EVENT))
        val dateNews = getString(getColumnIndex(TableEvents.Cols.DATE_NEWS))
        val type = getString(getColumnIndex(TableEvents.Cols.TYPE))
        val icon = getString(getColumnIndex(TableEvents.Cols.ICON))
        val state = getInt(getColumnIndex(TableEvents.Cols.STATE))

        val event = EventData()
        event.code=code
        event.title=title
        event.text=text
        event.dateevent=dateEvent?.substring(0,10)
        event.datenews=dateNews?.substring(0,10)
        event.type=type
        event.icon=icon
        event.state=state

        return event
    }

    fun getVotingData():VotingData{
        val code = getInt(getColumnIndex(TableVoting.Cols.CODE))
        val event = getInt(getColumnIndex(TableVoting.Cols.EVENT))
        val text = getString(getColumnIndex(TableVoting.Cols.TEXT))

        val votingData = VotingData()
        votingData.code = code
        votingData.event = event
        votingData.text = text

        return votingData
    }

    fun getPossibleData() : PossibleData{
        val code = getInt(getColumnIndex(TablePossibles.Cols.CODE))
        val voting = getInt(getColumnIndex(TablePossibles.Cols.VOTING))
        val text = getString(getColumnIndex(TablePossibles.Cols.TEXT))

        val possibleData = PossibleData()
        possibleData.code = code
        possibleData.voting = voting
        possibleData.text = text

        return possibleData

    }

    fun getAnswerData() : AnswerData {
        val size = getInt(getColumnIndex(TableAnswers.Cols.SIZE))
        val voting = getInt(getColumnIndex(TableAnswers.Cols.VOTING))
        val text = getString(getColumnIndex(TableAnswers.Cols.TEXT))

        val answerData = AnswerData()
        answerData.size = size
        answerData.voting = voting
        answerData.text = text

        return answerData

    }

    fun getMessageData():MessageData{
        val code = getInt(getColumnIndex(TableMessages.Cols.CODE))
        val event = getInt(getColumnIndex(TableMessages.Cols.EVENT))
        val date = getString(getColumnIndex(TableMessages.Cols.DATE))
        val author = getString(getColumnIndex(TableMessages.Cols.AUTHOR))
        val authorName = getString(getColumnIndex(TableMessages.Cols.AUTHOR_NAME))
        val parentMessage = getInt(getColumnIndex(TableMessages.Cols.PARENT_MESSAGE))
        val text = getString(getColumnIndex(TableMessages.Cols.TEXT))
        val uuid = getString(getColumnIndex(TableMessages.Cols.UUID))
        val state = getInt(getColumnIndex(TableMessages.Cols.STATE))
        val verification = getInt(getColumnIndex(TableMessages.Cols.VERIFICATION))

        val messageData = MessageData()

        messageData.code = code
        messageData.event = event
        messageData.date = date
        messageData.author = author
        messageData.authorname = authorName
        messageData.parentmessage = parentMessage
        messageData.text = text
        messageData.uuid = uuid
        messageData.state = state
        messageData.verification = verification

        return messageData
    }


}
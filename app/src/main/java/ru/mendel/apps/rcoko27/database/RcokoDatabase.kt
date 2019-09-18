package ru.mendel.apps.rcoko27.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ru.mendel.apps.rcoko27.data.*
import ru.mendel.apps.rcoko27.database.RcokoDbSchema.*

class RcokoDatabase(context: Context) {

    init {
        if (!isInit){
            mDatabase = RcokoDbHelper(context).writableDatabase
            isInit = true
        }
    }

    companion object {
        var isInit = false
        private lateinit var mDatabase: SQLiteDatabase


        fun setEvent(event: EventData){
            val thisEvent = getEvent(event.code)
            if (thisEvent!=null){
                updateEvent(event)
            }else{
                insertEvent(event)
            }
        }

        fun getEvent(code:Int):EventData?{
            val where = TableEvents.Cols.CODE + "=" +code
            val cursor = queryEventData(where,null)
            try {
                cursor.moveToFirst()
                if (!cursor.isAfterLast){
                    val eventData = cursor.getEventData()
                    return eventData
                }else{
                    return null
                }

            }finally {
                cursor.close()
            }
        }

        private fun insertEvent(event:EventData){
            val cv = getContentValues(event)
            mDatabase.insert(TableEvents.NAME,null,cv)
        }

        private fun updateEvent(event: EventData){
            val cv = getContentValues(event)
            val where = TableEvents.Cols.CODE+"="+event.code
            mDatabase.update(TableEvents.NAME,cv,where,null)
        }

        fun getVoting(eventCode: Int):MutableList<VotingData>{
            val data = mutableListOf<VotingData>()

            val where = TableVoting.Cols.EVENT+"=$eventCode"
            val curVoting = queryVotingData(where,null)

            curVoting.moveToFirst()
            while (!curVoting.isAfterLast){
                val voting = curVoting.getVotingData()
                voting.possibles = getPossibles(voting.code)
                voting.answers = getAnswers(voting.code)
                data.add(voting)
                curVoting.moveToNext()
            }

            return data
        }

        fun getPossibles(votingCode: Int):MutableList<PossibleData>{
            val data = mutableListOf<PossibleData>()
            val where = TablePossibles.Cols.VOTING+"=$votingCode"
            val curPossible = queryPossibleData(where,null)
            curPossible.moveToFirst()
            while (!curPossible.isAfterLast){
                data.add(curPossible.getPossibleData())
                curPossible.moveToNext()
            }
            return data
        }

        fun getAnswers(votingCode: Int):MutableList<AnswerData>{
            val data = mutableListOf<AnswerData>()
            val where = TableAnswers.Cols.VOTING+"=$votingCode"
            val curAnswer = queryAnswerData(where,null)
            curAnswer.moveToFirst()
            while (!curAnswer.isAfterLast){
                data.add(curAnswer.getAnswerData())
                curAnswer.moveToNext()
            }
            return data
        }

        fun getMessages(eventCode: Int):MutableList<MessageData>{
            val data = mutableListOf<MessageData>()

            val where = TableVoting.Cols.EVENT+"=$eventCode"
            val curMessages = queryMessageData(where,null)

            curMessages.moveToFirst()
            while (!curMessages.isAfterLast){
                val message = curMessages.getMessageData()
                data.add(message)
                curMessages.moveToNext()
            }

            return data
        }

        fun clearVoting(eventCode: Int){
            val where = TableVoting.Cols.EVENT+"=$eventCode"
            //сначала берем все голосования
            val curVoting = mDatabase.query(TableVoting.NAME,
                null,
                where,
                null,
                null,
                null,
                null)
            curVoting.moveToFirst()
            while (!curVoting.isAfterLast){
                //удаляем все варианты
                val code = curVoting.getInt(curVoting.getColumnIndex(TableVoting.Cols.CODE))//код голосования

                val wherePossible = TablePossibles.Cols.VOTING+"=$code"
                mDatabase.delete(TablePossibles.NAME,wherePossible,null)

                val whereAnswer = TableAnswers.Cols.VOTING+"=$code"
                mDatabase.delete(TableAnswers.NAME,whereAnswer,null)

                curVoting.moveToNext()
            }
            //удаляем сами голосования
            mDatabase.delete(TableVoting.NAME,where, null)
        }

        fun clearMessages(eventCode: Int){
            val where = TableMessages.Cols.EVENT+"=$eventCode"
            mDatabase.delete(TableMessages.NAME,where, null)
        }

        fun insertVoting(voting: VotingData){
            val cv = Companion.getContentValues(voting)
            mDatabase.beginTransaction()
            for (possible in voting.possibles){
                val cvPossible = Companion.getContentValues(possible)
                mDatabase.insert(TablePossibles.NAME,null,cvPossible)
            }
            for (answer in voting.answers){
                val cvAnswer = Companion.getContentValues(answer)
                mDatabase.insert(TableAnswers.NAME,null,cvAnswer)
            }
            mDatabase.insert(TableVoting.NAME,null,cv)
            mDatabase.setTransactionSuccessful()
            mDatabase.endTransaction()

        }

        fun insertMessage(message: MessageData){
            val cv = Companion.getContentValues(message)
            mDatabase.insert(TableMessages.NAME,null,cv)
        }

        fun updateMessage(message: MessageData){
            val cv = Companion.getContentValues(message)
            val where = TableMessages.Cols.AUTHOR+"='"+message.author+"' "+
                    " AND "+TableMessages.Cols.UUID+"='"+message.uuid+"'"
            mDatabase.update(TableMessages.NAME,cv,where,null)
        }

        private fun queryEventData(whereClause: String?, whereArgs: Array<String>?): RcokoCursorWrapper {
            val cursor = mDatabase.query(
                TableEvents.NAME, //группировка
                null, //список колонок
                whereClause, //условие
                whereArgs,
                null,
                null, //having
                TableEvents.Cols.DATE_EVENT + " DESC"//сортировка
            )
            return RcokoCursorWrapper(cursor)
        }

        private fun queryVotingData(whereClause: String?, whereArgs: Array<String>?): RcokoCursorWrapper {
            val cursor = mDatabase.query(
                TableVoting.NAME, //группировка
                null, //список колонок
                whereClause, //условие
                whereArgs,
                null,
                null, //having
                TableVoting.Cols.CODE//сортировка
            )
            return RcokoCursorWrapper(cursor)
        }

        private fun queryPossibleData(whereClause: String?, whereArgs: Array<String>?): RcokoCursorWrapper {
            val cursor = mDatabase.query(
                TablePossibles.NAME, //группировка
                null, //список колонок
                whereClause, //условие
                whereArgs,
                null,
                null, //having
                TablePossibles.Cols.TEXT//сортировка
            )
            return RcokoCursorWrapper(cursor)
        }

        private fun queryAnswerData(whereClause: String?, whereArgs: Array<String>?): RcokoCursorWrapper {
            val cursor = mDatabase.query(
                TableAnswers.NAME, //группировка
                null, //список колонок
                whereClause, //условие
                whereArgs,
                null,
                null, //having
                TableAnswers.Cols.TEXT//сортировка
            )
            return RcokoCursorWrapper(cursor)
        }

        private fun queryMessageData(whereClause: String?, whereArgs: Array<String>?): RcokoCursorWrapper {
            val cursor = mDatabase.query(
                TableMessages.NAME, //группировка
                null, //список колонок
                whereClause, //условие
                whereArgs,
                null,
                null, //having
                TableMessages.Cols.CODE//сортировка
            )
            return RcokoCursorWrapper(cursor)
        }

        private fun getContentValues(event:EventData):ContentValues{
            val cv = ContentValues()
            cv.put(TableEvents.Cols.CODE,event.code)
            cv.put(TableEvents.Cols.TITLE,event.title)
            cv.put(TableEvents.Cols.TEXT,event.text)
            cv.put(TableEvents.Cols.DATE_EVENT,event.dateevent)
            cv.put(TableEvents.Cols.DATE_NEWS,event.datenews)
            cv.put(TableEvents.Cols.TYPE,event.type)
            cv.put(TableEvents.Cols.ICON,event.icon)
            cv.put(TableEvents.Cols.STATE,event.state)

            return cv
        }

        private fun getContentValues(voting: VotingData):ContentValues{
            val cv = ContentValues()
            cv.put(TableVoting.Cols.CODE,voting.code)
            cv.put(TableVoting.Cols.EVENT,voting.event)
            cv.put(TableVoting.Cols.TEXT,voting.text)

            return cv
        }

        private fun getContentValues(possible: PossibleData):ContentValues{
            val cv = ContentValues()
            cv.put(TablePossibles.Cols.CODE,possible.code)
            cv.put(TablePossibles.Cols.VOTING,possible.voting)
            cv.put(TablePossibles.Cols.TEXT,possible.text)

            return cv
        }

        private fun getContentValues(answer: AnswerData):ContentValues{
            val cv = ContentValues()
            cv.put(TableAnswers.Cols.VOTING,answer.voting)
            cv.put(TableAnswers.Cols.TEXT,answer.text)
            cv.put(TableAnswers.Cols.SIZE,answer.size)

            return cv
        }

        private fun getContentValues(message: MessageData):ContentValues{
            val cv = ContentValues()
            cv.put(TableMessages.Cols.CODE,message.code)
            cv.put(TableMessages.Cols.EVENT,message.event)
            cv.put(TableMessages.Cols.AUTHOR,message.author)
            cv.put(TableMessages.Cols.AUTHOR_NAME,message.authorname)
            cv.put(TableMessages.Cols.PARENT_MESSAGE,message.parentmessage)
            cv.put(TableMessages.Cols.TEXT,message.text)
            cv.put(TableMessages.Cols.UUID,message.uuid)
            cv.put(TableMessages.Cols.STATE,message.state)
            cv.put(TableMessages.Cols.VERIFICATION, message.verification)
            val date = if (message.date!=null && message.date!="none"){
                MessageData.convertDate(message.date!!,message.gmt!!)
            }else{
                "none"
            }
            System.out.println(date)
            cv.put(TableMessages.Cols.DATE,date)

            return cv
        }

    }

}
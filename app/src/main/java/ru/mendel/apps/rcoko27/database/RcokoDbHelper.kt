package ru.mendel.apps.rcoko27.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.mendel.apps.rcoko27.database.RcokoDbSchema.*

//class SuperChatDbHelper(context: Context) : SQLiteOpenHelper(context=context,name= DATABASE_NAME,factory=null,version= VERSION) {
class RcokoDbHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,VERSION) {
    //если это унаследовано от java класса то не нужно указывать имена параметров

    companion object {
        private const val VERSION = 1
        private const val DATABASE_NAME = "rcoko27.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db==null) return

        db.execSQL("create table " + TableEvents.NAME + "(" +
                TableEvents.Cols.CODE +", "+
                TableEvents.Cols.TITLE+", "+
                TableEvents.Cols.TEXT+", "+
                TableEvents.Cols.DATE_EVENT+", "+
                TableEvents.Cols.DATE_NEWS+", "+
                TableEvents.Cols.TYPE+", "+
                TableEvents.Cols.ICON+", "+
                TableEvents.Cols.STATE+
                ")")

        db.execSQL("create table " + TableVoting.NAME + "(" +
                TableVoting.Cols.CODE +", "+
                TableVoting.Cols.EVENT +", "+
                TableVoting.Cols.TEXT +
                ")")

        db.execSQL("create table " + TablePossibles.NAME + "(" +
                TablePossibles.Cols.CODE +", "+
                TablePossibles.Cols.VOTING +", "+
                TablePossibles.Cols.TEXT +
                ")")

        db.execSQL("create table " + TableMessages.NAME + "(" +
                TableMessages.Cols.CODE +", "+
                TableMessages.Cols.EVENT +", "+
                TableMessages.Cols.DATE +", "+
                TableMessages.Cols.AUTHOR +", "+
                TableMessages.Cols.AUTHOR_NAME +", "+
                TableMessages.Cols.PARENT_MESSAGE +", "+
                TableMessages.Cols.TEXT +", "+
                TableMessages.Cols.UUID +", "+
                TableMessages.Cols.STATE +
                ")")

        db.execSQL("create table " + TableAnswers.NAME + "(" +
                TableAnswers.Cols.VOTING +", "+
                TableAnswers.Cols.TEXT +", "+
                TableAnswers.Cols.SIZE +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db==null) return
    }
}
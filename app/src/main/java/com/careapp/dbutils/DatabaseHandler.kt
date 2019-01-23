package com.careapp.dbutils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.careapp.BuildConfig

class DatabaseHandler private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    protected var lock = Any()

    override fun close() {
        super.close()
        mInstance = null
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TableAnganwadiCenters.CREATE_TABLE)
        db.execSQL(TableCategories.CREATE_TABLE)
        db.execSQL(TableQuestions.CREATE_TABLE)
        db.execSQL(TableAnswerGroup.CREATE_TABLE)
        db.execSQL(TableAnswerInput.CREATE_TABLE)
        db.execSQL(TableAnswers.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            if (oldVersion != DATABASE_VERSION) {
                db.execSQL("DROP TABLE IF EXISTS " + TableAnganwadiCenters.TABLE_NAME)
                db.execSQL("DROP TABLE IF EXISTS " + TableCategories.TABLE_NAME)
                db.execSQL("DROP TABLE IF EXISTS " + TableQuestions.TABLE_NAME)
                db.execSQL("DROP TABLE IF EXISTS " + TableAnswerGroup.TABLE_NAME)
                db.execSQL("DROP TABLE IF EXISTS " + TableAnswerInput.TABLE_NAME)
                db.execSQL("DROP TABLE IF EXISTS " + TableAnswers.TABLE_NAME)
                onCreate(db)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase? {
        if (writableDb == null || !writableDb!!.isOpen) {
            writableDb = super.getWritableDatabase()
        }
        return writableDb
    }

    override fun getReadableDatabase(): SQLiteDatabase? {
        if (writableDb == null || !writableDb!!.isOpen) {
            writableDb = super.getReadableDatabase()
        }
        return writableDb
    }

    @Synchronized
    fun clearTable(tableName: String): Int {
        return writableDatabase!!.delete(tableName, null, null)
    }

    @Synchronized
    fun clearDB() {
        writableDatabase!!.execSQL("delete from " + TableAnganwadiCenters.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableCategories.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableQuestions.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswerGroup.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswerInput.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswers.TABLE_NAME)
    }

    @Synchronized
    fun clearAllTables() {
        writableDatabase!!.execSQL("delete from " + TableAnganwadiCenters.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableCategories.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableQuestions.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswerGroup.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswerInput.TABLE_NAME)
        writableDatabase!!.execSQL("delete from " + TableAnswers.TABLE_NAME)
    }

    @Synchronized
    fun clearSingleTable(tableName: String) {
        writableDatabase!!.execSQL("DELETE FROM $tableName;")
    }

    fun insertData(tableName: String, values: ContentValues): Long {
        return writableDatabase!!.insert(tableName, null, values)
    }

    fun updateData(tableName: String, values: ContentValues, where: String, args: Array<String>): Int {
        return writableDatabase!!.update(tableName, values, where, args)
    }

    fun deleteData(tableName: String, where: String, args: Array<String>?): Int {
        return writableDatabase!!.delete(tableName, where, args)
    }

    fun selectData(tableName: String): Cursor {
        return writableDatabase!!.query(tableName, null, null, null, null, null, null)
    }

    fun selectData(tableName: String, where: String, args: Array<String>): Cursor {
        return writableDatabase!!.query(tableName, null, where, args, null, null, null)
    }

    fun selectData(selectQuery: String, rowQuery: Boolean): Cursor {
        return writableDatabase!!.rawQuery(selectQuery, null)
    }

    companion object {

        private var writableDb: SQLiteDatabase? = null

        private val DATABASE_VERSION = BuildConfig.VERSION_CODE

        var DATABASE_NAME = "CareAppDb"
        var mInstance: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler {
            if (mInstance == null) {
                mInstance = DatabaseHandler(context)
                mInstance!!.writableDatabase
            }
            return mInstance as DatabaseHandler
        }
    }
}

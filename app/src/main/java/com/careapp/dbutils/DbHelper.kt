package com.careapp.dbutils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.text.TextUtils
import com.careapp.models.*

class DbHelper(context: Context) {

    private val databaseHandler: DatabaseHandler

    init {
        databaseHandler = DatabaseHandler.getInstance(context)
    }

    /**
     * This is categories table part
     */

    @Synchronized
    fun addCategory(category: CategoriesModel): Long {
        databaseHandler.getWritableDatabase()
        val values = ContentValues()
        values.put(TableCategories.categoryId, category.id)
        values.put(TableCategories.name, category.name)
        values.put(TableCategories.rank, category.rank)
        try {
            return databaseHandler.insertData(TableCategories.TABLE_NAME, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @Synchronized
    fun getCategory(categoryId: String): CategoriesModel {
        val categoriesModel = CategoriesModel()
        val selectQuery =
            "select * FROM " + TableCategories.TABLE_NAME + " WHERE " + TableCategories.categoryId + "='" + categoryId + "'"
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                categoriesModel.id = cursor.getString(cursor.getColumnIndex(TableCategories.categoryId))
                categoriesModel.rank = cursor.getInt(cursor.getColumnIndex(TableCategories.rank))
                categoriesModel.name = cursor.getString(cursor.getColumnIndex(TableCategories.name))
            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return categoriesModel
    }

    val getCategoriesSize: String
        @Synchronized get() = DatabaseUtils.stringForQuery(
            databaseHandler.getReadableDatabase(),
            "SELECT COUNT(*) FROM " + TableCategories.TABLE_NAME, null
        )

    val getCategories: ArrayList<CategoriesModel>
        @Synchronized get() {
            val arrayList = ArrayList<CategoriesModel>()
            val selectQuery =
                "select * FROM " + TableCategories.TABLE_NAME + " ORDER BY " + TableCategories.rank + " ASC"
            databaseHandler.getReadableDatabase()
            val cursor = databaseHandler.selectData(selectQuery, true)
            if (cursor.moveToFirst()) {
                do {
                    val categoriesModel = CategoriesModel()
                    categoriesModel.id = cursor.getString(cursor.getColumnIndex(TableCategories.categoryId))
                    categoriesModel.rank = cursor.getInt(cursor.getColumnIndex(TableCategories.rank))
                    categoriesModel.name = cursor.getString(cursor.getColumnIndex(TableCategories.name))
                    arrayList.add(categoriesModel)
                } while (cursor.moveToNext())
            }
            if (!cursor.isClosed()) {
                cursor.close()
            }
            return arrayList
        }

    /**
     * This is anganwadi centers part
     */

    @Synchronized
    fun addAnganwadiCenter(anganwadiModel: AnganwadiModel): Long {
        databaseHandler.getWritableDatabase()
        val values = ContentValues()
        values.put(TableAnganwadiCenters.anganwadiId, anganwadiModel.anganwadiId)
        values.put(TableAnganwadiCenters.name, anganwadiModel.name)
        values.put(TableAnganwadiCenters.total, anganwadiModel.total)
        values.put(TableAnganwadiCenters.completed, anganwadiModel.completed)
        try {
            return databaseHandler.insertData(TableAnganwadiCenters.TABLE_NAME, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @Synchronized
    fun getAnganwadiCenter(anganwadiId: String): AnganwadiModel {
        val anganwadiModel = AnganwadiModel()
        val selectQuery =
            "select * FROM " + TableAnganwadiCenters.TABLE_NAME + " WHERE " + TableAnganwadiCenters.anganwadiId + "='" + anganwadiId + "'"
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                anganwadiModel.anganwadiId = cursor.getString(cursor.getColumnIndex(TableAnganwadiCenters.anganwadiId))
                anganwadiModel.total = cursor.getInt(cursor.getColumnIndex(TableAnganwadiCenters.total))
                anganwadiModel.completed = cursor.getInt(cursor.getColumnIndex(TableAnganwadiCenters.completed))
                anganwadiModel.name = cursor.getString(cursor.getColumnIndex(TableAnganwadiCenters.name))
            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return anganwadiModel
    }

    val getAnganwadiCentersSize: String
        @Synchronized get() = DatabaseUtils.stringForQuery(
            databaseHandler.getReadableDatabase(),
            "SELECT COUNT(*) FROM " + TableAnganwadiCenters.TABLE_NAME,
            null
        )

    val getAnganwadiCenters: ArrayList<AnganwadiModel>
        @Synchronized get() {
            val arrayList = ArrayList<AnganwadiModel>()
            val selectQuery =
                "select * FROM " + TableAnganwadiCenters.TABLE_NAME
            databaseHandler.getReadableDatabase()
            val cursor = databaseHandler.selectData(selectQuery, true)
            if (cursor.moveToFirst()) {
                do {
                    val anganwadiModel = AnganwadiModel()
                    anganwadiModel.anganwadiId =
                            cursor.getString(cursor.getColumnIndex(TableAnganwadiCenters.anganwadiId))
                    anganwadiModel.total = cursor.getInt(cursor.getColumnIndex(TableAnganwadiCenters.total))
                    anganwadiModel.completed = cursor.getInt(cursor.getColumnIndex(TableAnganwadiCenters.completed))
                    anganwadiModel.name = cursor.getString(cursor.getColumnIndex(TableAnganwadiCenters.name))
                    arrayList.add(anganwadiModel)
                } while (cursor.moveToNext())
            }
            if (!cursor.isClosed()) {
                cursor.close()
            }
            return arrayList
        }


    /**
     * This is Questions table part
     */

    @Synchronized
    fun addQuestion(questionsModel: QuestionsModel): Long {
        val answerGroupIds = getAnswerGroupIds(questionsModel.answerGroupsArrayList!!)
        databaseHandler.getWritableDatabase()
        val values = ContentValues()
        values.put(TableQuestions.questionId, questionsModel.questionId)
        values.put(TableQuestions.rank, questionsModel.rank)
        values.put(TableQuestions.body, questionsModel.body)
        values.put(TableQuestions.hint, questionsModel.hint)
        values.put(TableQuestions.categoryId, questionsModel.categoryId)
        values.put(TableQuestions.categoryName, questionsModel.categoryName)
        values.put(TableQuestions.conditionalDisplay, questionsModel.conditionalDisplay)
        values.put(TableQuestions.houseSelectable, questionsModel.houseSelectable)
        values.put(TableQuestions.houseSelectionPreference, questionsModel.houseSelectionPreference)
        values.put(TableQuestions.parentQuestionId, questionsModel.parentQuestionId)
        values.put(TableQuestions.answerGroupsId, answerGroupIds)
        values.put(TableQuestions.isAnswered, questionsModel.isAnswered.toString())
        try {
            return databaseHandler.insertData(TableQuestions.TABLE_NAME, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @Synchronized
    fun getQuestion(questionId: String): QuestionsModel {
        val questionsModel = QuestionsModel()
        val selectQuery =
            "select * FROM " + TableQuestions.TABLE_NAME + " WHERE " + TableQuestions.questionId + "='" + questionId + "'"
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                questionsModel.questionId = cursor.getString(cursor.getColumnIndex(TableQuestions.questionId))
                questionsModel.rank = cursor.getInt(cursor.getColumnIndex(TableQuestions.rank))
                questionsModel.categoryId = cursor.getString(cursor.getColumnIndex(TableQuestions.categoryId))
                questionsModel.categoryName = cursor.getString(cursor.getColumnIndex(TableQuestions.categoryName))
                questionsModel.body = cursor.getString(cursor.getColumnIndex(TableQuestions.body))
                questionsModel.hint = cursor.getString(cursor.getColumnIndex(TableQuestions.hint))
                questionsModel.conditionalDisplay =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.conditionalDisplay)).equals("true", true)
                questionsModel.houseSelectable =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.houseSelectable)).equals("true", true)
                questionsModel.houseSelectionPreference =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.houseSelectionPreference))
                questionsModel.parentQuestionId =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.parentQuestionId))
                questionsModel.answerGroupsArrayList =
                        getAnswerGroups(cursor.getString(cursor.getColumnIndex(TableQuestions.answerGroupsId)))
                questionsModel.answerGroupsArrayList!!.sortedWith(compareBy { it.rank })

            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return questionsModel
    }

    val getQuestionsSize: String
        @Synchronized get() = DatabaseUtils.stringForQuery(
            databaseHandler.getReadableDatabase(),
            "SELECT COUNT(*) FROM " + TableQuestions.TABLE_NAME, null
        )

    @Synchronized
    fun getQuestionsForACategory(categoryId: String): ArrayList<QuestionsModel> {
        val arrayList = ArrayList<QuestionsModel>()
        val selectQuery =
            "select * FROM " + TableQuestions.TABLE_NAME + " WHERE " + TableQuestions.categoryId + "='" + categoryId + "'" + " ORDER BY " + TableQuestions.rank + " ASC"
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                val questionsModel = QuestionsModel()
                questionsModel.questionId = cursor.getString(cursor.getColumnIndex(TableQuestions.questionId))
                questionsModel.rank = cursor.getInt(cursor.getColumnIndex(TableQuestions.rank))
                questionsModel.categoryId = cursor.getString(cursor.getColumnIndex(TableQuestions.categoryId))
                questionsModel.categoryName = cursor.getString(cursor.getColumnIndex(TableQuestions.categoryName))
                questionsModel.body = cursor.getString(cursor.getColumnIndex(TableQuestions.body))
                questionsModel.hint = cursor.getString(cursor.getColumnIndex(TableQuestions.hint))
                questionsModel.conditionalDisplay =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.conditionalDisplay))
                            .equals("true", true)
                questionsModel.houseSelectable =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.houseSelectable)).equals("true", true)
                questionsModel.houseSelectionPreference =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.houseSelectionPreference))
                questionsModel.parentQuestionId =
                        cursor.getString(cursor.getColumnIndex(TableQuestions.parentQuestionId))
                questionsModel.answerGroupsArrayList =
                        getAnswerGroups(cursor.getString(cursor.getColumnIndex(TableQuestions.answerGroupsId)))
                questionsModel.answerGroupsArrayList!!.sortedWith(compareBy { it.rank })
                val isAns = cursor.getString(cursor.getColumnIndex(TableQuestions.isAnswered))
                questionsModel.isAnswered = if (!TextUtils.isEmpty(isAns) && isAns.equals("true", true)) true else false
                arrayList.add(questionsModel)
            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return arrayList
    }

    private fun getAnswerGroupIds(answerGroupsArrayList: ArrayList<AnswerGroupsModel>): String {
        if (answerGroupsArrayList.isEmpty()) return ""
        var answerGroupIds = ""
        for (answerGroup in answerGroupsArrayList) {
            answerGroupIds = answerGroupIds + "," + addAnswerGroup(answerGroup)
        }
        return answerGroupIds.removePrefix(",")
    }

    private fun getAnswerInputIds(answerInputsArrayList: ArrayList<AnswerInputModel>): String {
        if (answerInputsArrayList.isEmpty()) return ""
        var answerInputIds = ""
        for (answerInput in answerInputsArrayList) {
            answerInputIds = answerInputIds + "," + addAnswerInput(answerInput)
        }
        return answerInputIds.removePrefix(",")
    }

    private fun addAnswerGroup(answerGroup: AnswerGroupsModel): Long {
        val answerInputIds = getAnswerInputIds(answerGroup.answerInputsArrayList!!)
        databaseHandler.getWritableDatabase()
        val values = ContentValues()
        values.put(TableAnswerGroup.answerGroupId, answerGroup.answerGroupId)
        values.put(TableAnswerGroup.rank, answerGroup.rank)
        values.put(TableAnswerGroup.label, answerGroup.label)
        values.put(TableAnswerGroup.mandatory, answerGroup.mandatory.toString())
        values.put(TableAnswerGroup.multiple, answerGroup.multiple)
        values.put(TableAnswerGroup.childQuesDisplayTriggerInputId, answerGroup.childQuesDisplayTriggerInputId)
        values.put(TableAnswerGroup.nextGroupDisplayTriggerInputId, answerGroup.nextGroupDisplayTriggerInputId)
        values.put(TableAnswerGroup.answerInputIds, answerInputIds)
        values.put(TableAnswerGroup.answeredInputId, answerGroup.answeredInputId)
        values.put(TableAnswerGroup.answer, answerGroup.answer)
        try {
            return databaseHandler.insertData(TableAnswerGroup.TABLE_NAME, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    private fun addAnswerInput(answerInput: AnswerInputModel): Long {
        databaseHandler.getWritableDatabase()
        val values = ContentValues()
        values.put(TableAnswerInput.answerInputId, answerInput.answerInputId)
        values.put(TableAnswerInput.label, answerInput.label)
        values.put(TableAnswerInput.displayChildQuestionsOnSelect, answerInput.displayChildQuestionsOnSelect)
        values.put(TableAnswerInput.displayNextGroupOnSelect, answerInput.displayNextGroupOnSelect)
        values.put(TableAnswerInput.inputType, answerInput.inputType)
        values.put(TableAnswerInput.answer, answerInput.answer)
        try {
            return databaseHandler.insertData(TableAnswerInput.TABLE_NAME, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @Synchronized
    fun getAnswerGroup(answerGroupId: String): AnswerGroupsModel {
        val answerGroupsModel = AnswerGroupsModel()
        val selectQuery =
            "select * FROM " + TableAnswerGroup.TABLE_NAME + " WHERE " + TableAnswerGroup.Z_PK + "='" + answerGroupId + "'" /*+ " ORDER BY " + TableAnswerGroup.rank + " ASC"*/
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                answerGroupsModel.answerGroupId =
                        cursor.getString(cursor.getColumnIndex(TableAnswerGroup.answerGroupId))
                answerGroupsModel.rank = cursor.getInt(cursor.getColumnIndex(TableAnswerGroup.rank))
                answerGroupsModel.label = cursor.getString(cursor.getColumnIndex(TableAnswerGroup.label))
                answerGroupsModel.childQuesDisplayTriggerInputId =
                        cursor.getString(cursor.getColumnIndex(TableAnswerGroup.childQuesDisplayTriggerInputId))
                answerGroupsModel.mandatory =
                        cursor.getString(cursor.getColumnIndex(TableAnswerGroup.mandatory)).equals("true", true)
                answerGroupsModel.multiple = cursor.getString(cursor.getColumnIndex(TableAnswerGroup.multiple))
                answerGroupsModel.nextGroupDisplayTriggerInputId =
                        cursor.getString(cursor.getColumnIndex(TableAnswerGroup.nextGroupDisplayTriggerInputId))
                answerGroupsModel.answerInputsArrayList?.addAll(
                    getAnswerInputs(
                        cursor.getString(
                            cursor.getColumnIndex(
                                TableAnswerGroup.answerInputIds
                            )
                        )
                    )
                )
                answerGroupsModel.answer = cursor.getString(cursor.getColumnIndex(TableAnswerGroup.answer))
                answerGroupsModel.answeredInputId =
                        cursor.getString(cursor.getColumnIndex(TableAnswerGroup.answeredInputId))
            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return answerGroupsModel
    }

    @Synchronized
    fun getAnswerInput(answerInputId: String): AnswerInputModel {
        val answerInputModel = AnswerInputModel()
        val selectQuery =
            "select * FROM " + TableAnswerInput.TABLE_NAME + " WHERE " + TableAnswerInput.Z_PK + "='" + answerInputId + "'"
        databaseHandler.getReadableDatabase()
        val cursor = databaseHandler.selectData(selectQuery, true)
        if (cursor.moveToFirst()) {
            do {
                answerInputModel.answerInputId =
                        cursor.getString(cursor.getColumnIndex(TableAnswerInput.answerInputId))
                answerInputModel.inputType =
                        cursor.getInt(cursor.getColumnIndex(TableAnswerInput.inputType))
                answerInputModel.answer =
                        cursor.getString(cursor.getColumnIndex(TableAnswerInput.answer))
                answerInputModel.isSelected = false
                answerInputModel.label = cursor.getString(cursor.getColumnIndex(TableAnswerInput.label))
                answerInputModel.displayChildQuestionsOnSelect =
                        cursor.getString(cursor.getColumnIndex(TableAnswerInput.displayChildQuestionsOnSelect))
                            .equals("true", true)
                answerInputModel.displayNextGroupOnSelect =
                        cursor.getString(cursor.getColumnIndex(TableAnswerInput.displayNextGroupOnSelect))
                            .equals("true", true)
            } while (cursor.moveToNext())
        }
        if (!cursor.isClosed()) {
            cursor.close()
        }
        return answerInputModel
    }

    private fun getAnswerInputs(answerInputIds: String): ArrayList<AnswerInputModel> {
        val answerInputsArrayList = ArrayList<AnswerInputModel>()
        val answerInputIdsArray = answerInputIds.split(",")
        for (id in answerInputIdsArray) {
            answerInputsArrayList.add(getAnswerInput(id))
        }
        return answerInputsArrayList
    }

    private fun getAnswerGroups(answerGroupIds: String): ArrayList<AnswerGroupsModel> {
        val answerGroupsArrayList = ArrayList<AnswerGroupsModel>()
        val answerGroupIdsArray = answerGroupIds.split(",")
        for (id in answerGroupIdsArray) {
            answerGroupsArrayList.add(getAnswerGroup(id))
        }
        return answerGroupsArrayList
    }

    @Synchronized
    fun selectData(tableName: String): Cursor {
        databaseHandler.getReadableDatabase()
        return databaseHandler.selectData(tableName)
    }

    fun closeDb() {
        databaseHandler.clearDB()
        databaseHandler.close()
    }

    @Synchronized
    fun deleteAllOrders(): Int {
        return databaseHandler.deleteData(TableAnganwadiCenters.TABLE_NAME, "", null)
    }

    @Synchronized
    fun deleteItem(itemId: String) {
        databaseHandler.getWritableDatabase()
        databaseHandler.deleteData(
            TableAnganwadiCenters.TABLE_NAME,
            TableAnganwadiCenters.anganwadiId + "=?",
            arrayOf(itemId)
        )
    }

    @Synchronized
    fun deleteAll() {
        databaseHandler.clearAllTables()
        databaseHandler.close()
    }

}

package com.careapp.dbutils

internal object TableAnswerInput {

    var Z_PK: String

    var answerInputId: String? = null
    var label: String? = null
    var inputType: String? = null
    var answer: String? = null
    var displayNextGroupOnSelect: String? = null
    var displayChildQuestionsOnSelect: String? = null

    val TABLE_NAME = "TableAnswerInput"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        displayChildQuestionsOnSelect = "zDisplayChildQuestionsOnSelect"
        displayNextGroupOnSelect = "zDisplayNextGroupOnSelect"
        answerInputId = "zAnswerInputId"
        label = "zLabel"
        inputType = "zInputType"
        answer = "zAnswer"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $inputType INTEGER, " +
                "$label TEXT, $answerInputId TEXT, $displayNextGroupOnSelect TEXT, $answer TEXT, $displayChildQuestionsOnSelect TEXT, UNIQUE ($answerInputId) ON CONFLICT REPLACE);"

    }

}
package com.careapp.dbutils

internal object TableAnswerGroup {

    var Z_PK: String

    var answerGroupId: String? = null
    var rank: String? = null
    var label: String? = null
    var mandatory: String? = null
    var multiple: String? = null
    var nextGroupDisplayTriggerInputId: String? = null
    var childQuesDisplayTriggerInputId: String? = null
    var answerInputIds: String? = null
    var answeredInputId: String? = null
    var answer: String? = null

    val TABLE_NAME = "TableAnswerGroup"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        answerGroupId = "zAnswerGroupId"
        rank = "zRank"
        label = "zLabel"
        mandatory = "zMandatory"
        multiple = "zMultiple"
        nextGroupDisplayTriggerInputId = "zNextGroupDisplayTriggerInputId"
        childQuesDisplayTriggerInputId = "zChildQuesDisplayTriggerInputId"
        answerInputIds = "zAnswerInputIds"
        answeredInputId = "zAnsweredInputId"
        answer = "zAnswer"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $rank INTEGER, $answerGroupId TEXT, $nextGroupDisplayTriggerInputId TEXT, $childQuesDisplayTriggerInputId TEXT, $answerInputIds TEXT, $label TEXT, $mandatory TEXT, $answeredInputId TEXT, $answer TEXT, $multiple TEXT, UNIQUE ($answerGroupId) ON CONFLICT REPLACE);"

    }

}
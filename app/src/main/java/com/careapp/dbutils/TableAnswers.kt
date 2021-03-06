package com.careapp.dbutils

internal object TableAnswers {

    var Z_PK: String

    var anganwadiId: String? = null
    var questionId: String? = null
    var answerId: String? = null
    var houseId: String? = null
    var answer: String? = null
    var recordedAt: String? = null
    var location: String? = null


    val TABLE_NAME = "TableAnswers"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        anganwadiId = "zAnganwadiId"
        questionId = "zQuestionId"
        answerId = "zAnswerId"
        answer = "zAnswer"
        houseId = "zHouseId"
        recordedAt = "zRecordedAt"
        location = "zLocation"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $answer TEXT, $anganwadiId TEXT, $answerId TEXT, $questionId TEXT, $recordedAt TEXT, $location TEXT, $houseId TEXT);"

    }

}

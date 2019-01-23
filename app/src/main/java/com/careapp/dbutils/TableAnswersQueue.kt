package com.careapp.dbutils

internal object TableAnswersQueue {

    var Z_PK: String

    var anganwadiId: String? = null
    var questionId: String? = null
    var answerId: String? = null
    var houseId: String? = null
    var answer: String? = null

    val TABLE_NAME = "TableAnswersQueue"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        anganwadiId = "zAnganwadiId"
        questionId = "zQuestionId"
        answerId = "zAnswerId"
        answer = "zAnswer"
        houseId = "zHouseId"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $answer TEXT, $anganwadiId TEXT, $answerId TEXT, $questionId TEXT, $houseId TEXT, UNIQUE ($answerId) ON CONFLICT REPLACE);"

    }

}

package com.careapp.dbutils

internal object TableAnswers {

    var Z_PK: String

    var anganwadiId: String? = null
    var questionId: String? = null
    var answerId: String? = null

    val TABLE_NAME = "TableAnswers"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        anganwadiId = "zAnganwadiId"
        questionId = "zQuestionId"
        answerId = "zAnswerId"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $anganwadiId TEXT, $answerId TEXT, $questionId TEXT);"

    }

}

package com.careapp.dbutils

internal object TableAnswersGroup {

    var Z_PK: String

    var anganwadiId: String? = null
    var questionId: String? = null
    var groupId: String? = null

    val TABLE_NAME = "TableAnswersGroup"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        anganwadiId = "zAnganwadiId"
        questionId = "zQuestionId"
        groupId = "zGroupId"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $anganwadiId TEXT, $groupId TEXT, $questionId TEXT);"

    }

}

package com.careapp.dbutils

internal object TableAnganwadiCenters {

    var Z_PK: String

    var anganwadiId: String? = null
    var name: String? = null
    var total: String? = null
    var completed: String? = null

    val TABLE_NAME = "TableAnganwadiCenter"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        anganwadiId = "zAnganwadiId"
        name = "zName"
        total = "zTotal"
        completed = "zCompleted"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $anganwadiId TEXT, $name TEXT, $total INTEGER, $completed INTEGER, UNIQUE ($anganwadiId) ON CONFLICT REPLACE);"

    }

}

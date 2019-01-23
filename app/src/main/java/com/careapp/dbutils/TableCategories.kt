package com.careapp.dbutils

internal object TableCategories {

    var Z_PK: String

    var categoryId: String? = null
    var name: String? = null
    var rank: String? = null

    val TABLE_NAME = "TableCategory"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        categoryId = "zCategoryId"
        name = "zName"
        rank = "zRank"

        CREATE_TABLE =
                "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $categoryId TEXT, $name TEXT, $rank INTEGER, UNIQUE ($categoryId) ON CONFLICT REPLACE);"

    }

}
/*{"id":"5c2c9d441331be1a0f5dc7ba","name":"संसाधन","rank":1}*/
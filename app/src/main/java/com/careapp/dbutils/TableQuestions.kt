package com.careapp.dbutils

internal object TableQuestions {

    var Z_PK: String

    var questionId: String? = null
    var rank: String? = null
    var body: String? = null
    var hint: String? = null
    var categoryId: String? = null
    var categoryName: String? = null
    var conditionalDisplay: String? = null
    var houseSelectable: String? = null
    var houseSelectionPreference: String? = null
    var parentQuestionId: String? = null
    var answerGroupsId: String? = null
    var isAnswered: String? = null

    val TABLE_NAME = "TableQuestion"
    var CREATE_TABLE: String

    init {

        Z_PK = "_id"
        categoryId = "zCategoryId"
        categoryName = "zCategoryName"
        questionId = "zQuestionId"
        rank = "zRank"
        body = "zBody"
        conditionalDisplay = "zConditionalDisplay"
        houseSelectable = "zHouseSelectable"
        houseSelectionPreference = "zHouseSelectionPreference"
        parentQuestionId = "zParentQuestionId"
        hint = "zHint"
        answerGroupsId = "zAnswerGroupsId"
        isAnswered = "zIsAnswered"

        CREATE_TABLE = "create table if not exists $TABLE_NAME ( $Z_PK INTEGER PRIMARY KEY NOT NULL, $rank INTEGER, " +
                "$questionId TEXT, $houseSelectionPreference TEXT, $parentQuestionId TEXT, $body TEXT, $hint TEXT, $answerGroupsId TEXT, " +
                "$categoryId TEXT, $categoryName TEXT, $conditionalDisplay TEXT, $isAnswered TEXT, $houseSelectable TEXT, UNIQUE ($questionId) ON CONFLICT REPLACE);"

    }

}

/*
{
      "id": "5c2c9d441331be1a0f5dc7bb",
      "rank": 1,
      "body": "क्या भ्रमण के समय केंद्र खुला है ?",
      "hint": "यदि नही तो यही समाप्त करे।",
      "categoryId": "5c2c9d441331be1a0f5dc7ba",
      "categoryName": "संसाधन",
      "conditionalDisplay": false,
      "houseSelectable": false,
      "houseSelectionPreference": null,
      "parentQuestionId": null,
      "answerGroups": [
        {
          "id": "5c2c9d441331be1a0f5dc7bc",
          "rank": 2,
          "label": "स्थिति ",
          "mandatory": true,
          "multiple": null,
          "nextGroupDisplayTriggerInputId": null,
          "childQuesDisplayTriggerInputId": null,
          "answerInputs": [
            {
              "id": "5c2c9d441331be1a0f5dc7bd",
              "label": "हाँ ",
              "inputType": 2,
              "displayNextGroupOnSelect": null,
              "displayChildQuestionsOnSelect": null
            },
            {
              "id": "5c2c9d441331be1a0f5dc7be",
              "label": " नही",
              "inputType": 2,
              "displayNextGroupOnSelect": null,
              "displayChildQuestionsOnSelect": null
            }
          ]
        }
      ]
    }
        */
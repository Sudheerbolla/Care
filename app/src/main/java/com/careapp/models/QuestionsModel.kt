package com.careapp.models

import com.google.gson.JsonObject
import java.io.Serializable

class QuestionsModel : Serializable {

    var questionId: String? = null
    var categoryId: String? = null
    var categoryName: String? = null
    var body: String? = null
    var hint: String? = null
    var rank: Int? = 0
    var conditionalDisplay: Boolean? = false
    var houseSelectable: Boolean? = false
    var houseSelectionPreference: String? = ""
    var parentQuestionId: String? = ""
    var answerGroupsArrayList: ArrayList<AnswerGroupsModel>? = null
    var isAnswered: Boolean = false

    constructor(jsonObject: JsonObject) {
        questionId =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        categoryId =
                if (jsonObject.has("categoryId") && !jsonObject.get("categoryId").isJsonNull) jsonObject.get("categoryId").asString else ""
        categoryName =
                if (jsonObject.has("categoryName") && !jsonObject.get("categoryName").isJsonNull) jsonObject.get("categoryName").asString else ""
        body =
                if (jsonObject.has("body") && !jsonObject.get("body").isJsonNull) jsonObject.get("body").asString else ""
        hint =
                if (jsonObject.has("hint") && !jsonObject.get("hint").isJsonNull) jsonObject.get("hint").asString else ""
        rank =
                if (jsonObject.has("rank") && !jsonObject.get("rank").isJsonNull)
                    jsonObject.get(
                        "rank"
                    ).asInt else 0
        houseSelectionPreference =
                if (jsonObject.has("houseSelectionPreference") && !jsonObject.get("houseSelectionPreference").isJsonNull)
                    jsonObject.get("houseSelectionPreference").asString else ""
        parentQuestionId =
                if (jsonObject.has("parentQuestionId") && !jsonObject.get("parentQuestionId").isJsonNull)
                    jsonObject.get("parentQuestionId").asString else ""
        conditionalDisplay =
                if (jsonObject.has("conditionalDisplay") && !jsonObject.get("conditionalDisplay").isJsonNull)
                    jsonObject.get("conditionalDisplay").asBoolean else false
        houseSelectable =
                if (jsonObject.has("houseSelectable") && !jsonObject.get("houseSelectable").isJsonNull)
                    jsonObject.get("houseSelectable").asBoolean else false

        if (jsonObject.has("answerGroups") && !jsonObject.get("answerGroups").isJsonNull) {
            answerGroupsArrayList = ArrayList()
            val answerGroups = jsonObject.getAsJsonArray("answerGroups")
            if (answerGroups.size() > 0) {
                for (answerGroup in answerGroups) {
                    answerGroupsArrayList?.add(AnswerGroupsModel(answerGroup as JsonObject))
                }
            }
        } else answerGroupsArrayList = ArrayList()
    }

    constructor() {
        answerGroupsArrayList = ArrayList()
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
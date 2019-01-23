package com.careapp.models

import com.google.gson.JsonObject
import java.io.Serializable

class AnswerInputModel : Serializable {

    var answerInputId: String? = ""
    var label: String? = ""
    var inputType: Int? = 0
    var displayNextGroupOnSelect: Boolean? = false
    var displayChildQuestionsOnSelect: Boolean? = false
    var isSelected: Boolean? = false
    var answer: String? = ""

    constructor(jsonObject: JsonObject) {
        answerInputId =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        label =
                if (jsonObject.has("label") && !jsonObject.get("label").isJsonNull) jsonObject.get("label").asString else ""
        inputType =
                if (jsonObject.has("inputType") && !jsonObject.get("inputType").isJsonNull)
                    jsonObject.get(
                        "inputType"
                    ).asInt else 0
        displayChildQuestionsOnSelect =
                if (jsonObject.has("displayChildQuestionsOnSelect") && !jsonObject.get("displayChildQuestionsOnSelect").isJsonNull)
                    jsonObject.get("displayChildQuestionsOnSelect").asBoolean else false
        displayNextGroupOnSelect =
                if (jsonObject.has("displayNextGroupOnSelect") && !jsonObject.get("displayNextGroupOnSelect").isJsonNull)
                    jsonObject.get("displayNextGroupOnSelect").asBoolean else false
    }

    constructor() {}

}
package com.careapp.models

import com.google.gson.JsonObject
import java.io.Serializable

class AnswerGroupsModel : Serializable {

    var answerGroupId: String? = null
    var label: String? = null
    var rank: Int? = 0
    var mandatory: Boolean? = false
    var multiple: String? = ""
    var nextGroupDisplayTriggerInputId: String = ""
    var childQuesDisplayTriggerInputId: String = ""
    var answerInputsArrayList: ArrayList<AnswerInputModel>? = null
    var answeredInputId: String? = ""
    var answer: String? = ""

    constructor(jsonObject: JsonObject) {
        answerGroupId =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        label =
                if (jsonObject.has("label") && !jsonObject.get("label").isJsonNull) jsonObject.get("label").asString else ""
        rank =
                if (jsonObject.has("rank") && !jsonObject.get("rank").isJsonNull)
                    jsonObject.get(
                        "rank"
                    ).asInt else 0
        multiple =
                if (jsonObject.has("multiple") && !jsonObject.get("multiple").isJsonNull)
                    jsonObject.get("multiple").asString else ""
        mandatory =
                if (jsonObject.has("mandatory") && !jsonObject.get("mandatory").isJsonNull)
                    jsonObject.get("mandatory").asBoolean else false
        nextGroupDisplayTriggerInputId =
                if (jsonObject.has("nextGroupDisplayTriggerInputId") && !jsonObject.get("nextGroupDisplayTriggerInputId").isJsonNull)
                    jsonObject.get("nextGroupDisplayTriggerInputId").asString else ""
        childQuesDisplayTriggerInputId =
                if (jsonObject.has("childQuesDisplayTriggerInputId") && !jsonObject.get("childQuesDisplayTriggerInputId").isJsonNull)
                    jsonObject.get("childQuesDisplayTriggerInputId").asString else ""
        if (jsonObject.has("answerInputs") && !jsonObject.get("answerInputs").isJsonNull) {
            answerInputsArrayList = ArrayList()
            val answerInputs = jsonObject.getAsJsonArray("answerInputs")
            if (answerInputs.size() > 0) {
                for (answerGroup in answerInputs) {
                    answerInputsArrayList?.add(AnswerInputModel(answerGroup as JsonObject))
                }
            }
        } else answerInputsArrayList = ArrayList()
    }

    constructor() {
        answerInputsArrayList = ArrayList()
    }

}
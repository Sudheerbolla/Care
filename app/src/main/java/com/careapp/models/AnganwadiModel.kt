package com.careapp.models

import com.google.gson.JsonObject

class AnganwadiModel {

    var anganwadiId: String? = null
    var name: String? = null
    var total: Int? = 0
    var completed: Int? = 0

    constructor(jsonObject: JsonObject) {
        anganwadiId =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        name =
                if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull) jsonObject.get("name").asString else ""
        total =
                if (jsonObject.has("selectedHouseholdCount") && !jsonObject.get("selectedHouseholdCount").isJsonNull)
                    jsonObject.get(
                        "selectedHouseholdCount"
                    ).asInt else 0
        completed =
                if (jsonObject.has("completedHouseholdCount") && !jsonObject.get("completedHouseholdCount").isJsonNull
                )
                    jsonObject.get(
                        "completedHouseholdCount"
                    ).asInt else 0
    }

    override fun toString(): String {
//        return super.toString()
        return name!!
    }

    constructor() {
    }

}

/*{
    "id": "5c2918af1331be2ee3cfc46e",
    "name": "abc center",
    "selectedHouseholdCount": 0,
    "completedHouseholdCount": 0
}*/
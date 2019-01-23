package com.careapp.models

import com.google.gson.JsonObject

class CategoriesModel {

    var id: String? = null
    var name: String? = null
    var rank: Int? = 0

    constructor(jsonObject: JsonObject) {
        id =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        name =
                if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull) jsonObject.get("name").asString else ""
        rank =
                if (jsonObject.has("rank") && !jsonObject.get("rank").isJsonNull) jsonObject.get("rank").asInt else 0
    }

    constructor() {}

}

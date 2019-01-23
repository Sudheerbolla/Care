package com.careapp.models

import com.google.gson.JsonObject

class HomesModel {

    var id: String? = null
    var name: String? = null

    constructor(jsonObject: JsonObject) {
        id =
                if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull) jsonObject.get("id").asString else ""
        name =
                if (jsonObject.has("name") && !jsonObject.get("name").isJsonNull) jsonObject.get("name").asString else ""
    }

    constructor() {}

}

package com.samoreque.freshlypressed.api.converters

import android.net.Uri
import java.lang.reflect.Type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

class UriDeserializer : JsonDeserializer<Uri> {

    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }
}
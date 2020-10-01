package com.samoreque.freshlypressed.api.converters

import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

class DateDeserializer : JsonDeserializer<Date> {

    companion object {
        private const val FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    }

    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
    ): Date? = json?.let { SimpleDateFormat(FORMAT, Locale.US).parse(it.asString) }
}
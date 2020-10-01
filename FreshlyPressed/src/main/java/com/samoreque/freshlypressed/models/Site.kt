package com.samoreque.freshlypressed.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Represents the [Site] data model of WordPress the API
 */
@Keep
data class Site(
        @SerializedName("ID")
        val id: Int,
        val name: String,
        @SerializedName("subscribers_count")
        val subscribersCount: Int
)
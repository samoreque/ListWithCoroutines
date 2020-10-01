package com.samoreque.freshlypressed.models

import android.net.Uri

import java.util.Date

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

/**
 * Represents the [Post] data model of the WordPress API
 */
@Keep
data class Post(
        @SerializedName("ID")
        val id: Int,
        val title: String,
        val excerpt: String,
        val author: Author,
        val date: Date,
        @SerializedName("URL")
        val url: Uri,
        @SerializedName("featured_image")
        val featureImageUrl: String = ""
)

@Keep
data class Author(
        val name: String,
        @SerializedName("URL")
        val url: Uri,
        @SerializedName("avatar_URL")
        val avatarUrl: String = "",
        var subscribedCount: Int = 0
)
package com.samoreque.freshlypressed.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PostRequest(
        val found: Long = 0,
        val posts: List<Post> = emptyList(),
        val meta: Meta = Meta()
)

@Keep
data class Meta(
        @SerializedName("next_page")
        val  nextPage: String = ""
)
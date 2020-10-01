package com.samoreque.freshlypressed.api

import com.samoreque.freshlypressed.models.PostRequest
import com.samoreque.freshlypressed.models.Site
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordPressApi {
    @GET("sites/{url}")
    suspend fun loadSubscribersCount(@Path("url") url: String): Site


    @GET("sites/discover.wordpress.com/posts")
    suspend fun loadPosts(@Query("number") number: Int,
                          @Query("page_handle") pageHandle: String): PostRequest
}
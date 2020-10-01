package com.samoreque.freshlypressed.respository

import android.net.Uri
import com.samoreque.freshlypressed.models.PostRequest
import com.samoreque.freshlypressed.models.Site

/**
 * Defines the operations allowed to perform operations on WordPress resources.
 */
interface WordPressRepository {
    suspend fun loadPosts(number: Int = 20, page: String= ""): PostRequest
    suspend fun loadSubscribersCount(url: Uri?): Site?
}
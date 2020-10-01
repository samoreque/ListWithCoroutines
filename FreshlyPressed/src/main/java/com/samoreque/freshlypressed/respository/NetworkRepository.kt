package com.samoreque.freshlypressed.respository

import android.net.Uri
import com.samoreque.freshlypressed.api.PostsRequestException
import com.samoreque.freshlypressed.api.WordPressApi
import com.samoreque.freshlypressed.models.PostRequest
import com.samoreque.freshlypressed.models.Site
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Perform the operations for the WordPress resources form the network.
 * @property wordPressApi perform the API request through Retrofit
 */
class NetworkRepository(private val wordPressApi: WordPressApi,
                             private val dispatcher: CoroutineDispatcher = Dispatchers.IO)
    : WordPressRepository {

    override suspend fun loadPosts(number: Int, page: String): PostRequest {
        return withContext(dispatcher) {
            try {
                val postRequest = wordPressApi.loadPosts(number, page)
                postRequest
            } catch (e: Throwable) {
                throw PostsRequestException(e)
            }
        }
    }

    override suspend fun loadSubscribersCount(url: Uri?): Site? {
        return withContext(dispatcher) {
            url?.host?.let {
                loadSubscribersCountInternal(it)
            }
        }
    }

    private suspend fun loadSubscribersCountInternal(url: String): Site? {
        return wordPressApi.loadSubscribersCount(url)
    }
}



package com.samoreque.freshlypressed.ui

import android.net.Uri
import android.util.Log

import java.lang.Exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import com.samoreque.freshlypressed.exceptions.EmptyPostsException
import com.samoreque.freshlypressed.exceptions.LoadMoreException
import com.samoreque.freshlypressed.models.Post
import com.samoreque.freshlypressed.models.PostRequest
import com.samoreque.freshlypressed.models.Site
import com.samoreque.freshlypressed.respository.WordPressRepository

/**
 * Handles the Posts events
 */
class PostsViewModel(private val repository: WordPressRepository) : ViewModel() {

    companion object {
        private val TAG = PostsViewModel::class.java.name

        /**
         * Factory for creating [PostsViewModel]
         *
         * @param arg the repository to pass to [PostsViewModel]
         */
        @JvmStatic
        val factory = singleArgViewModelFactory(::PostsViewModel)
    }

    val subscriberCache = mutableMapOf<String, Site?>()

    var postsCache: MutableList<Post>? = null

    var postsRequest: PostRequest = PostRequest()
        set(value) {
            postsCache = postsCache ?: mutableListOf()
            postsCache?.addAll(value.posts)
            field = value
        }

    private val _postList = MutableLiveData<List<Post>>()

    val postList: LiveData<List<Post>>
        get() = _postList

    private val _postUpdated = MutableLiveData<Int>()

    val postUpdated: LiveData<Int>
        get() = _postUpdated

    private val _refreshSpinner = MutableLiveData<Boolean>()

    val refreshSpinner: LiveData<Boolean>
        get() = _refreshSpinner

    private val _spinner = MutableLiveData(false)

    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _error = MutableLiveData<Throwable>()

    /**
     * Indicates if there is an error
     */
    val error: LiveData<Throwable>
        get() = _error

    /**
     * Fetch the posts, showing a loading spinner while it displayed.
     */
    fun loadPosts(isRefresh: Boolean = false) = launchDataLoad(isRefresh) {
        postsRequest = repository.loadPosts()
        if (!postsRequest.posts.isNullOrEmpty()) {
            _postList.value = postsCache
        } else {
            throw EmptyPostsException()
        }

    }

    override fun onCleared() {
        super.onCleared()
    }

    fun refreshPost() {
        postsCache = mutableListOf()
        loadPosts(true)
    }

    private fun getSubscribers(url: Uri?): Int? {
        return url?.host?.let { safeUrl ->
            subscriberCache[safeUrl]?.subscribersCount
        }
    }

    private fun fetchSubscribersCount(position: Int, url: Uri?) {
        viewModelScope.launch {
            try {
                url?.host?.let { host ->
                    val site = repository.loadSubscribersCount(url)
                    subscriberCache[host] = site
                    _postUpdated.value = position
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error", e)
            }
        }
    }

    fun loadSubscribersCount(position: Int, url: Uri?): String {
        val count = getSubscribers(url)
        if (count == null) {
            fetchSubscribersCount(position, url)
        }
        return count?.let { it.toString() } ?: "-"
    }

    /**
     * Helper function to call a data load function with a loading spinner, errors will trigger a
     * [_error].
     *
     * By marking `block` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling the
     *              lambda the loading spinner will display, after completion or error the loading
     *              spinner will stop
     */
    private fun launchDataLoad(isRefresh: Boolean, block: suspend () -> Unit) {
        val spinnerToUse = if (isRefresh) _refreshSpinner else _spinner
        val handler = CoroutineExceptionHandler { _, exception ->
            _error.value = exception
        }
        viewModelScope.launch(context = handler) {
            try {
                spinnerToUse.value = true
                block()
            } catch (error: Throwable) {
                _error.value = error
            } finally {
                spinnerToUse.value = false
            }
        }
    }

    fun onLoadMore() {
        viewModelScope.launch {
            try {
                postsRequest = repository.loadPosts(page = postsRequest.meta.nextPage)
                _postList.value = postsCache
            } catch (throwable: Throwable) {
                _error.value = LoadMoreException(throwable)
            }
        }
    }

    fun errorHandled() {
        if (error.value != null) {
            _error.value = null
        }
    }
}

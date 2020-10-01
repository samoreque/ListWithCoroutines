package com.samoreque.freshlypressed

import android.net.Uri
import com.samoreque.freshlypressed.models.*

import java.util.Date

object PostsTestUtils {
    fun getPostRequest(size: Int) = PostRequest(
            found = 100L,
            posts = (1..size).map { getPost(it) },
            meta = Meta(nextPage = "next_page")
    )

    private fun getPost(id: Int) = Post(id, "title$id", "excerpt$id", getAuthor(id), Date(),
            Uri.parse("https://www.post$id.com/"))

    private fun getAuthor(id: Int) = Author("author$id", Uri.parse("https://www.author$id.com/"))

    fun getSite(id : Int = 1, count: Int) = Site(id, "nameSite$id", count)

}
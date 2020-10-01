package com.samoreque.freshlypressed.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samoreque.freshlypressed.PostUtils.isSameDateRepresentation
import com.samoreque.freshlypressed.models.Post
import com.samoreque.freshlypressed.ui.PostViewHolder.Companion.create
import com.bumptech.glide.RequestManager

class PostsAdapter(private val glide: RequestManager,
                   var posts: MutableList<Post> = mutableListOf(),
                   private val subscribersCountListener: OnSubscribersCount)
    : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return create(parent, glide)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val previousItem = if (position > 0) posts[position - 1] else null
        val post = posts[position]
        val isNewDay = previousItem == null || !isSameDateRepresentation(previousItem.date, post.date)
        val count: String = subscribersCountListener.getSubscribersCount(position, post)
        holder.bind(post, isNewDay, count)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    interface OnSubscribersCount {
        fun getSubscribersCount(position: Int, post: Post): String
    }
}

package com.samoreque.freshlypressed.ui

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.samoreque.freshlypressed.PostUtils.printDate
import com.samoreque.freshlypressed.R
import com.samoreque.freshlypressed.models.Post
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * A RecyclerView ViewHolder that displays a post.
 */
class PostViewHolder(view: View, private val glide: RequestManager)
    : RecyclerView.ViewHolder(view) {
    private val title = itemView.findViewById<TextView>(R.id.title)
    private val summary = itemView.findViewById<TextView>(R.id.summary)
    private val author = itemView.findViewById<TextView>(R.id.author_name)
    private val postCount = itemView.findViewById<TextView>(R.id.subscribers_count)
    private val url = itemView.findViewById<TextView>(R.id.urlHost)
    private var header = itemView.findViewById<TextView>(R.id.header)
    private val postImage = itemView.findViewById<ImageView>(R.id.postImage)
    private val authorAvatar = itemView.findViewById<ImageView>(R.id.authorImage)
    private val context = view.context

    fun bind(post: Post, isNewDay: Boolean, count: String) {
        header.visibility = if (isNewDay) View.VISIBLE else View.GONE
        val dateString = printDate(post.date)
        val urlInfo = "${post.url.host ?: ""} - $dateString"
        url.text = urlInfo

        header.text = dateString
        title.text = Html.fromHtml(post.title)
        summary.text = Html.fromHtml(post.excerpt)

        val postBy = context.getString(R.string.post_by)
        val authorName = "$postBy ${post.author.name}"
        val spannable = SpannableString(authorName)
        spannable.setSpan(StyleSpan(Typeface.BOLD), postBy.length, authorName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        author.text = spannable

        postCount.text = count
        val corner = context.resources.getDimension(R.dimen.image_corner).toInt()
        glide.load(post.featureImageUrl)
                .transform(CenterCrop(), RoundedCorners(corner))
                .placeholder(R.drawable.image_placeholder)
                .into(postImage)

        glide.load(post.author.avatarUrl)
                .circleCrop()
                .placeholder(R.mipmap.ic_wordpress)
                .into(authorAvatar)
        itemView.setOnClickListener {
            post.url.toString().let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent)
            }
        }

    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup, glide: RequestManager): PostViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_list_fragment_header_item, parent, false)
            return PostViewHolder(view, glide)
        }
    }
}
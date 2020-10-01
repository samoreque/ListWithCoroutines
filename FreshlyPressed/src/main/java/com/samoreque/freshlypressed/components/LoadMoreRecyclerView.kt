package com.samoreque.freshlypressed.components

import android.content.Context
import android.util.AttributeSet

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoadMoreRecyclerView: RecyclerView {

    companion object {
        private const val THRESHOLD = 5
    }

    private var isLoading: Boolean = false
    private var loadMoreCallback: OnLoadMoreListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initScrollListener()
    }

    private fun initScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() >= getItemSize() - THRESHOLD) {
                        loadMoreCallback?.onLoadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun getItemSize() = adapter?.itemCount ?: Integer.MAX_VALUE

    fun onLoadMoreListener(onLoadMore: OnLoadMoreListener) {
        this.loadMoreCallback = onLoadMore
    }
    fun loadMoreFinished() {
        isLoading = false
    }
    interface OnLoadMoreListener{
        fun onLoadMore()
    }
}
package com.animegatari.hayanime.ui.utils.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {
    fun RecyclerView.applyHorizontalPadding(position: Int, viewWidthPx: Int, itemWidthPx: Int, paddingView: Int) {
        val padding = (viewWidthPx - itemWidthPx - paddingView) / 2
        this.setPadding(padding, 0, padding, 0)
        this.scrollToPosition(position)
    }

    fun RecyclerView.setupHorizontalList(
        adapter: ListAdapter<*, *>,
        snapHelper: LinearSnapHelper,
        scrollListener: RecyclerView.OnScrollListener? = null,
    ) {
        this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter
        this.setHasFixedSize(true)
        this.clearOnScrollListeners()
        scrollListener?.let { this.addOnScrollListener(it) }
        snapHelper.attachToRecyclerView(this)
    }
}
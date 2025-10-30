package com.animegatari.hayanime.ui.utils.recyclerview

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.ui.adapter.PictureCarouselAdapter
import com.animegatari.hayanime.ui.utils.decorations.SpacingHorizontalDecoration
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.UncontainedCarouselStrategy

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

    fun carouselRecyclerView(
        recyclerView: RecyclerView,
        carouselAdapter: PictureCarouselAdapter,
    ) = with(recyclerView) {
        layoutManager = CarouselLayoutManager(UncontainedCarouselStrategy())
        CarouselSnapHelper().attachToRecyclerView(this@with)
        adapter = carouselAdapter
    }

    fun <T : RecyclerView.ViewHolder> Context.flexChipRecyclerView(
        recyclerView: RecyclerView,
        listAdapter: ListAdapter<*, T>,
    ) = with(recyclerView) {
        layoutManager = FlexboxLayoutManager(this@flexChipRecyclerView).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        adapter = listAdapter
    }

    fun <T : RecyclerView.ViewHolder> Context.horizontalSpacingRecyclerView(
        recyclerView: RecyclerView,
        listAdapter: ListAdapter<*, T>,
        spacing: Int,
        hasDivider: Boolean = false,
        divider: Drawable? = null,
    ) = with(recyclerView) {
        layoutManager = LinearLayoutManager(this@horizontalSpacingRecyclerView, LinearLayoutManager.HORIZONTAL, false)
        addItemDecoration(SpacingHorizontalDecoration(spacing, hasDivider, divider))
        adapter = listAdapter
    }
}
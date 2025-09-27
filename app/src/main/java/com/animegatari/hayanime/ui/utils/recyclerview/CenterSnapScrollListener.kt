package com.animegatari.hayanime.ui.utils.recyclerview

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class CenterSnapScrollListener(
    private val snapHelper: LinearSnapHelper,
    private val getItemValue: (position: Int) -> Int?,
    private val onItemSelected: (selectedValue: Int) -> Unit,
) : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState != RecyclerView.SCROLL_STATE_IDLE) return

        val layoutManager = recyclerView.layoutManager ?: return
        val snappedView = snapHelper.findSnapView(layoutManager) ?: return
        val snappedPosition = layoutManager.getPosition(snappedView)

        if (snappedPosition != RecyclerView.NO_POSITION) {
            getItemValue(snappedPosition)?.let(onItemSelected)
        }
    }
}
package com.animegatari.hayanime.ui.utils.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BottomPaddingItemDecoration(private val paddingBottom: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val lastItemPosition = parent.adapter?.itemCount?.minus(1)

        if (position == lastItemPosition) {
            outRect.bottom = paddingBottom
        }
    }
}
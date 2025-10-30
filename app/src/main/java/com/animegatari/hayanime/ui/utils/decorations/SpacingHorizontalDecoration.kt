package com.animegatari.hayanime.ui.utils.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingHorizontalDecoration(
    private val spacing: Int,
    private val showDivider: Boolean = false,
    private val divider: Drawable? = null,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val adapter = parent.adapter ?: return
        val position = parent.getChildAdapterPosition(view)
        val lastItemPosition = adapter.itemCount.minus(1)

        if (position == RecyclerView.NO_POSITION || position == lastItemPosition) {
            return
        }

        outRect.right = spacing
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        if (!showDivider) return
        divider ?: return

        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val halfSpacing = spacing / 2
            val dividerLeft = child.right + params.rightMargin + halfSpacing - (divider.intrinsicWidth / 2)
            val dividerRight = dividerLeft + divider.intrinsicWidth

            divider.setBounds(dividerLeft, top, dividerRight, bottom)
            divider.draw(c)
        }
    }
}
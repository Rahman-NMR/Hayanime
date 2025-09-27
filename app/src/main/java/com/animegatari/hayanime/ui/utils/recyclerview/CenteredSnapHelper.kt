package com.animegatari.hayanime.ui.utils.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CenteredSnapHelper : LinearSnapHelper() {
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val horizontalHelper: OrientationHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        val centerPosition = (horizontalHelper.startAfterPadding + horizontalHelper.endAfterPadding) / 2
        var closestChild: View? = null
        var minDistance = Int.MAX_VALUE

        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = horizontalHelper.getDecoratedStart(child) + horizontalHelper.getDecoratedMeasurement(child) / 2
            val distance = abs(centerPosition - childCenter)

            if (distance < minDistance) {
                minDistance = distance
                closestChild = child
            }
        }
        return closestChild
    }
}
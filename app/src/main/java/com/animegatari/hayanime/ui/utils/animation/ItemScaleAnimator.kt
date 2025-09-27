package com.animegatari.hayanime.ui.utils.animation

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import kotlin.math.abs

class ItemScaleAnimator(
    private val targetRecyclerView: RecyclerView,
    private val currentItemLabelTextView: TextView? = null,
    private val valueToLabelMap: Map<Int, String>? = null,
) : RecyclerView.OnScrollListener() {

    private val centerX: Float get() = targetRecyclerView.width / 2f
    private val context: Context get() = targetRecyclerView.context

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        updateItemScaleAndLabel()
    }

    private fun updateItemScaleAndLabel() {
        var minDistance = Float.MAX_VALUE
        var centerView: View? = null
        var centerViewPosition = RecyclerView.NO_POSITION

        for (i in 0 until targetRecyclerView.childCount) {
            val child = targetRecyclerView.getChildAt(i)
            val distanceFromCenter = calculateDistanceFromCenter(child)

            if (distanceFromCenter < minDistance) {
                minDistance = distanceFromCenter
                centerView = child
                centerViewPosition = targetRecyclerView.getChildAdapterPosition(child)
            }

            applyScalingAndAlpha(child, distanceFromCenter)
            styleNonCenterTextView(child)
        }

        centerView?.let {
            updateCenterItemLabel(centerViewPosition)
            styleCenterTextView(it)
        }
    }

    private fun calculateDistanceFromCenter(view: View): Float {
        val viewCenter = (view.left + view.right) / 2f
        return abs(viewCenter - centerX)
    }

    private fun applyScalingAndAlpha(view: View, distanceFromCenter: Float) {
        val scaleFactor = if (centerX > 0) 1f - (0.5f * distanceFromCenter / centerX) else 1f
        view.scaleX = scaleFactor
        view.scaleY = scaleFactor
        view.alpha = scaleFactor
    }

    private fun styleNonCenterTextView(view: View) {
        if (view is TextView) {
            view.background = null
            view.setTextColor(ContextCompat.getColor(context, R.color.text_color_secondary))
        }
    }

    private fun updateCenterItemLabel(itemValue: Int) {
        currentItemLabelTextView?.let { textView ->
            val label = valueToLabelMap?.get(itemValue)

            textView.text = label ?: context.getString(R.string.unknown_symbol)
            textView.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
            textView.backgroundTintList = ContextCompat.getColorStateList(context, R.color.md_theme_primary)
            textView.setBackgroundResource(R.drawable.bg_corner_16)
        }
    }

    private fun styleCenterTextView(view: View) {
        (view as? TextView)?.let {
            it.setBackgroundResource(R.drawable.bg_corner_16)
            it.backgroundTintList = ContextCompat.getColorStateList(context, R.color.md_theme_primary)
            it.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
        }
    }
}
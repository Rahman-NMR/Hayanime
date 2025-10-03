package com.animegatari.hayanime.ui.utils.recyclerview

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import java.lang.ref.WeakReference
import kotlin.math.abs

class CenterSnapScaleScrollListener(
    snapHelper: LinearSnapHelper,
    private val getItemValue: (position: Int) -> Int?,
    private val onItemSelected: (selectedValue: Int) -> Unit,
    currentItemLabelTextView: TextView? = null,
    private val valueToLabelMap: Map<Int, String>? = null,
) : RecyclerView.OnScrollListener() {

    private val snapHelperRef = WeakReference(snapHelper)
    private val labelViewRef = WeakReference(currentItemLabelTextView)

    private val layoutChangeListener = View.OnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
        val recyclerView = view as? RecyclerView ?: return@OnLayoutChangeListener
        updateItemScaleAndLabel(recyclerView)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        updateItemScaleAndLabel(recyclerView)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            notifyItemSelected(recyclerView)
        }
    }

    // Attach the layout change listener when this listener is attached to the RecyclerView
    fun attach(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(this)
        recyclerView.addOnLayoutChangeListener(layoutChangeListener)
    }

    // Detach the layout change listener when this listener is detached
    fun detach(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(this)
        recyclerView.removeOnLayoutChangeListener(layoutChangeListener)
    }

    private fun notifyItemSelected(recyclerView: RecyclerView) {
        val snapHelper = snapHelperRef.get() ?: return
        val layoutManager = recyclerView.layoutManager ?: return
        val snappedView = snapHelper.findSnapView(layoutManager) ?: return
        val snappedPosition = layoutManager.getPosition(snappedView)

        if (snappedPosition != RecyclerView.NO_POSITION) {
            getItemValue(snappedPosition)?.let(onItemSelected)
        }
    }

    private fun updateItemScaleAndLabel(recyclerView: RecyclerView) {
        val centerX = recyclerView.width / 2f
        if (centerX <= 0) return

        var minDistance = Float.MAX_VALUE
        var centerView: View? = null

        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val viewCenter = (child.left + child.right) / 2f
            val distanceFromCenter = abs(viewCenter - centerX)

            if (distanceFromCenter < minDistance) {
                minDistance = distanceFromCenter
                centerView = child
            }

            val scaleFactor = 1f - (0.5f * distanceFromCenter / centerX)
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
            child.alpha = scaleFactor

            styleNonCenterTextView(child, recyclerView.context)
        }

        centerView?.let {
            val centerPosition = recyclerView.getChildAdapterPosition(it)
            if (centerPosition != RecyclerView.NO_POSITION) {
                getItemValue(centerPosition)?.let { value ->
                    updateCenterItemLabel(value, recyclerView.context)
                }
            }
            styleCenterTextView(it, recyclerView.context)
        }
    }

    private fun styleNonCenterTextView(view: View, context: Context) {
        if (view is TextView) {
            view.background = null
            view.setTextColor(ContextCompat.getColor(context, R.color.text_color_secondary))
        }
    }

    private fun updateCenterItemLabel(itemValue: Int, context: Context) {
        labelViewRef.get()?.let { textView ->
            val label = valueToLabelMap?.get(itemValue)
            textView.text = label ?: context.getString(R.string.unknown_symbol)
            textView.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
            textView.backgroundTintList = ContextCompat.getColorStateList(context, R.color.md_theme_primary)
            textView.setBackgroundResource(R.drawable.bg_corner_16)
        }
    }

    private fun styleCenterTextView(view: View, context: Context) {
        (view as? TextView)?.let {
            it.setBackgroundResource(R.drawable.bg_corner_16)
            it.backgroundTintList = ContextCompat.getColorStateList(context, R.color.md_theme_primary)
            it.setTextColor(ContextCompat.getColor(context, R.color.md_theme_onPrimary))
        }
    }
}
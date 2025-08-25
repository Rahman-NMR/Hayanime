package com.animegatari.hayanime.ui.utils.layout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

object FabUtils {
    interface FirstVisibleItemDetailProvider {
        fun isFirstLogicalItemVisible(recyclerView: RecyclerView): Boolean
    }

    private class StaggeredGridDetailProvider : FirstVisibleItemDetailProvider {
        override fun isFirstLogicalItemVisible(recyclerView: RecyclerView): Boolean {
            val layoutManager = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return true
            val firstVisibleItemPosition = IntArray(layoutManager.spanCount)
            layoutManager.findFirstVisibleItemPositions(firstVisibleItemPosition)
            return firstVisibleItemPosition.any { it == 0 }
        }
    }

    private class LinearDetailProvider : FirstVisibleItemDetailProvider {
        override fun isFirstLogicalItemVisible(recyclerView: RecyclerView): Boolean {
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return true
            return layoutManager.findFirstVisibleItemPosition() == 0
        }
    }

    private class FabAwareScrollListener(
        private val fab: FloatingActionButton,
        private val detailProvider: FirstVisibleItemDetailProvider,
        private val recyclerView: RecyclerView,
    ) : RecyclerView.OnScrollListener() {
        private var isConsideredAtTop: Boolean = true
        private val isAdapterEmpty: Boolean get() = recyclerView.adapter == null || recyclerView.adapter?.itemCount == 0

        private fun evaluateFabVisibility() {
            if (isAdapterEmpty) {
                hideFabIfNecessary()
                isConsideredAtTop = true
                return
            }

            val isFirstLogicalItemVisible = detailProvider.isFirstLogicalItemVisible(recyclerView)

            when {
                isFirstLogicalItemVisible && !isConsideredAtTop -> {
                    hideFabIfNecessary()
                    isConsideredAtTop = true
                }

                !isFirstLogicalItemVisible && isConsideredAtTop -> {
                    showFabIfNecessary()
                    isConsideredAtTop = false
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            evaluateFabVisibility()
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                evaluateFabVisibility()
            }
        }

        fun updateInitialState() {
            recyclerView.post {
                isConsideredAtTop = isAdapterEmpty || detailProvider.isFirstLogicalItemVisible(recyclerView)

                if (isConsideredAtTop) {
                    hideFabIfNecessary()
                } else {
                    showFabIfNecessary()
                }
            }
        }

        private fun hideFabIfNecessary() {
            if (!fab.isOrWillBeHidden) fab.hide()
        }

        private fun showFabIfNecessary() {
            if (!fab.isOrWillBeShown) fab.show()
        }
    }

    fun attachFabScrollListener(recyclerView: RecyclerView, fab: FloatingActionButton) {
        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalArgumentException("LayoutManager cannot be null")

        val provider: FirstVisibleItemDetailProvider = when (layoutManager) {
            is StaggeredGridLayoutManager -> StaggeredGridDetailProvider()
            is LinearLayoutManager -> LinearDetailProvider()
            else -> throw IllegalArgumentException("Unsupported layout manager")
        }

        val scrollListener = FabAwareScrollListener(fab, provider, recyclerView)
        recyclerView.addOnScrollListener(scrollListener)
        scrollListener.updateInitialState()

        recyclerView.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                scrollListener.updateInitialState()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                scrollListener.updateInitialState()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                scrollListener.updateInitialState()
            }
        })
    }
}
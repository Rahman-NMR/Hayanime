package com.animegatari.hayanime.ui.utils.layout

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

object FabUtils {
    fun fabScrollBehavior(fab: FloatingActionButton) = object : RecyclerView.OnScrollListener() {
        var isAtTop = true

        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = rv.layoutManager as StaggeredGridLayoutManager
            val firstVisibleItemPositions = IntArray(layoutManager.spanCount)
            layoutManager.findFirstVisibleItemPositions(firstVisibleItemPositions)

            val isFirstItemVisible = firstVisibleItemPositions.any { it == 0 }

            when {
                isFirstItemVisible && !isAtTop -> {
                    fab.hide()
                    isAtTop = true
                }

                !isFirstItemVisible && isAtTop -> {
                    fab.show()
                    isAtTop = false
                }
            }
        }
    }
}
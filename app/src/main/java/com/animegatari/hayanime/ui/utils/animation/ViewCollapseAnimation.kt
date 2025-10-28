package com.animegatari.hayanime.ui.utils.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import androidx.core.view.isVisible

object ViewCollapseAnimation {
    const val ANIMATION_DURATION = 350L

    fun View.collapseAnimated(onAnimationCallback: () -> Unit) {
        val initHeight = this.measuredHeight

        val valueAnimator = ValueAnimator.ofInt(initHeight, 0)
        valueAnimator.addUpdateListener {
            val layoutParams = this.layoutParams
            layoutParams.height = it.animatedValue as Int
            this.layoutParams = layoutParams
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationCallback()
                this@collapseAnimated.isVisible = false
            }
        })
        valueAnimator.duration = ANIMATION_DURATION
        valueAnimator.start()
    }
}
package com.animegatari.hayanime.ui.utils.animation

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible

object ViewSlideInOutAnimation {
    const val ANIMATION_DURATION = 400L

    fun View.animateSlideDownAndHide() {
        this.animate()
            .translationY(this.height.toFloat())
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                this.isGone = true
            }
            .start()
    }

    fun View.animateSlideUpAndShow() {
        this.translationY = this.height.toFloat()
        this.isVisible = true

        this.animate()
            .translationY(0f)
            .setDuration(ANIMATION_DURATION)
            .start()
    }
}
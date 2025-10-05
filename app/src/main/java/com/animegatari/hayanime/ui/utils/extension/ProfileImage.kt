package com.animegatari.hayanime.ui.utils.extension

import android.graphics.drawable.Drawable
import android.view.Menu
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleCoroutineScope
import com.animegatari.hayanime.R
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.launch

object ProfileImage {
    fun Menu.loadProfileImage(
        glide: RequestManager,
        lifecycle: LifecycleCoroutineScope,
        profilePictureUrl: String?,
        @IdRes menuItemId: Int = R.id.menu_item_avatar,
    ) {
        if (profilePictureUrl.isNullOrBlank()) {
            return
        }

        glide.load(profilePictureUrl)
            .circleCrop()
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    lifecycle.launch {
                        findItem(menuItemId).icon = resource
                    }
                    return true
                }
            })
            .submit()
    }
}
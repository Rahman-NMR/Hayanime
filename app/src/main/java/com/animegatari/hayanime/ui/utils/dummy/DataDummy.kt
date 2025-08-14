package com.animegatari.hayanime.ui.utils.dummy

import android.content.Context
import kotlin.math.floor

object Dummy {
    val dummyData = listOf(
        "AVE",
        "The Adventures of Baron Munchausen",
        "Spirited Away from the Land of Gods",
        "Fairy Zero",
        "My Neighbor Totoro and the Magical Forest",
        "Transformer Legend of Cybertron: Rise of the Cybermen and Primeus Kingdom Cyber Luxuria Alicization",
        "Princess Mononoke's Epic Journey to Save Nature",
        "Howl's Moving Castle and the Sorcerer's Curse",
        "Narnia",
        "Grave of the Fireflies a Heartbreaking Tale of Survival",
        "The Wind Rises Dreams of Flight and Innovation",
        "Ponyo on the Cliff by the Sea a Magical Friendship",
    )

    fun calculateSpanCount(context: Context, columnWidthDp: Int): Int {
        if (columnWidthDp <= 0) return 1

        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

        val spanCount = floor(screenWidthDp / columnWidthDp).toInt()

        return if (spanCount > 0) spanCount else 1
    }
}
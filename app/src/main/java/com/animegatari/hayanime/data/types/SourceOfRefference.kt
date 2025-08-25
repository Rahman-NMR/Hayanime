package com.animegatari.hayanime.data.types

import androidx.annotation.StringRes
import com.animegatari.hayanime.R

enum class SourceOfRefference(@StringRes val stringResId: Int, val apiValue: String? = null) {
    OTHER(R.string.source_other, "other"),
    ORIGINAL(R.string.source_original, "original"),
    MANGA(R.string.source_manga, "manga"),
    FOUR_KOMA_MANGA(R.string.source_4_koma_manga, "4_koma_manga"),
    WEB_MANGA(R.string.source_web_manga, "web_manga"),
    DIGITAL_MANGA(R.string.source_digital_manga, "digital_manga"),
    NOVEL(R.string.source_novel, "novel"),
    LIGHT_NOVEL(R.string.source_light_novel, "light_novel"),
    VISUAL_NOVEL(R.string.source_visual_novel, "visual_novel"),
    GAME(R.string.source_game, "game"),
    CARD_GAME(R.string.source_card_game, "card_game"),
    BOOK(R.string.source_book, "book"),
    PICTURE_BOOK(R.string.source_picture_book, "picture_book"),
    RADIO(R.string.source_radio, "radio"),
    MUSIC(R.string.source_music, "music"),
    UNKNOWN(R.string.label_unknown);

    companion object {
        fun fromApiValue(apiValue: String?): SourceOfRefference {
            return entries.find { it.apiValue == apiValue?.lowercase() } ?: OTHER
        }
    }
}
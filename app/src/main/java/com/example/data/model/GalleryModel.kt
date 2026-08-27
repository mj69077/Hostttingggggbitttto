package com.example.data.model

data class IslamicGalleryItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "quran_cards", "hadith", "greetings", "mosques", "wallpapers"
    val categoryArabic: String,
    val textSnippet: String,
    val bgGradientStart: Long,
    val bgGradientEnd: Long,
    val iconResName: String = "ic_mosque_hero",
    val isDecorative: Boolean = true
)

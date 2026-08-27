package com.example.data.model

data class DuaItem(
    val id: Int,
    val title: String,
    val textArabic: String,
    val source: String,
    val category: String,
    val isFavorite: Boolean = false
)

data class AsmaAllahItem(
    val number: Int,
    val nameArabic: String,
    val meaningArabic: String,
    val explanation: String
)

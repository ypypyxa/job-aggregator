package ru.practicum.android.diploma.vacancy.filter.domain.model

data class Region(
    val id: String,
    val name: String,
    val parentId: String? = null
)

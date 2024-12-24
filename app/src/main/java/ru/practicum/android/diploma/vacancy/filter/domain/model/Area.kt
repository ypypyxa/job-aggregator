package ru.practicum.android.diploma.vacancy.filter.domain.model

data class Area(
    val id: String,
    val name: String,
    val parentId: String?,
    val parentName: String?,
    val areas: List<Area>
)

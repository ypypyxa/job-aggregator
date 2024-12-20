package ru.practicum.android.diploma.vacancy.search.domain.model

data class PagedData<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int
)

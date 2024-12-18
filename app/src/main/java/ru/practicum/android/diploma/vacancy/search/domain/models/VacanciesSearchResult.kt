package ru.practicum.android.diploma.vacancy.search.domain.models

data class VacanciesSearchResult(
    val items: List<Vacancy>,
    val found: Int,
    val page: Int,
    val pages: Int
)

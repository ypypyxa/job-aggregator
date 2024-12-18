package ru.practicum.android.diploma.vacancy.search.domain.model

data class VacancySearch(
    val id: Int,
    val name: String,
    val address: String?,
    val company: String?,
    val salary: String?,
    val logo: String?,
)

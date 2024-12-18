package ru.practicum.android.diploma.vacancy.search.domain.models

data class Salary(
    val currency: String?,
    val from: Int?,
    val to: Int?,
    val gross: Boolean
)

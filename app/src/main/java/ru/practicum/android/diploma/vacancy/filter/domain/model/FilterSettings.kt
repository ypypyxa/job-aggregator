package ru.practicum.android.diploma.vacancy.filter.domain.model

data class FilterSettings(
    val country: Country?,
    val region: Region?,
    val industry: Industry?,
    val expectedSalary: Int,
    val notShowWithoutSalary: Boolean
)

package ru.practicum.android.diploma.vacancy.details.domain

import ru.practicum.android.diploma.common.data.network.response.Salary

data class VacancyDetails(
    val id: Long,
    val name: String,
    val salary: Salary?,
    val areaName: String,
    val employerName: String,
    val employerLogoUrl240: String?,
)

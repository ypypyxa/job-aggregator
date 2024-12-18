package ru.practicum.android.diploma.vacancy.details.domain.models

import ru.practicum.android.diploma.common.data.network.response.Employer
import ru.practicum.android.diploma.common.data.network.response.Salary

data class VacancyDetails(
    val id: Long,
    val name: String,
    val salary: Salary?,
    val areaName: String,
    val employer: Employer,
    val experience: String?,
    val scheduleName: String,
    val description: String,
    val keySkills: List<String>,
    val address: String,
    val alternateUrl: String,
    val isFavorite: Boolean
)

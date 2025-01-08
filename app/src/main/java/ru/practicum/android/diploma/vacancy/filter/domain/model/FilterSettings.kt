package ru.practicum.android.diploma.vacancy.filter.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterSettings(
    val country: Country?,
    val region: Region?,
    val industry: Industry?,
    val expectedSalary: Int,
    val notShowWithoutSalary: Boolean
) : Parcelable

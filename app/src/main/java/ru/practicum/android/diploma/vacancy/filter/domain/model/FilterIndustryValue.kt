package ru.practicum.android.diploma.vacancy.filter.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterIndustryValue(
    val id: String?,
    val text: String?
) : Parcelable

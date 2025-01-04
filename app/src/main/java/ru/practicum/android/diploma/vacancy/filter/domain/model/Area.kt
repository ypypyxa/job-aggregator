package ru.practicum.android.diploma.vacancy.filter.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Area(
    val id: String,
    val name: String,
    val parentId: String?,
    val parentName: String?,
    val areas: List<Area>
): Parcelable

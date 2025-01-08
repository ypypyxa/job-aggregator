package ru.practicum.android.diploma.vacancy.filter.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Industry(
    val id: String,
    val name: String
) : Parcelable

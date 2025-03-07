package ru.practicum.android.diploma.common.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Общий класс для пар "ID-имя" (например, опыт работы, график работы).
 */
@Parcelize
data class IdNameDto(
    val id: String?,
    val name: String?
) : Parcelable

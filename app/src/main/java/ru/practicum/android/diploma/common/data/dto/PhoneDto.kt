package ru.practicum.android.diploma.common.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *     Городской код
 *      Комментарий
 *       Код страны
 *         Номер телефона
 */

@Parcelize
data class PhoneDto(
    val city: String?,
    val comment: String?,
    val country: String?,
    val number: String?,
) : Parcelable

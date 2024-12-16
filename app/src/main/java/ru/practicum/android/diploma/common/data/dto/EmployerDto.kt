package ru.practicum.android.diploma.common.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**Отображает данные о работодателе.*/
@Parcelize
data class EmployerDto(
    val id: Int,                    // Идентификатор работодателя
    val name: String,               // Название компании
    @SerializedName("logo_urls")
    val logoUrls: LogoDto?          // Логотипы компании
):Parcelable

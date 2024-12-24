package ru.practicum.android.diploma.common.data.dto

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

/*** Отображает данные о регионе или области. **/
data class AreasDto(
    val id: String,
    val name: String,
    @SerializedName("parent_id") val parentId: String?,
    val areas: List<AreasDto>?
)

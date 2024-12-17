package ru.practicum.android.diploma.common.data.dto

/**
 * Определяет индустрию и вложенные подиндустрии.
 */
data class IndustriesDto(
    val id: String,
    val name: String,
    val industries: List<IndustriesDto>?
)

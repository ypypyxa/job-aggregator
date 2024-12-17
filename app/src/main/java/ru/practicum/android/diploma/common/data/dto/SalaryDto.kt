package ru.practicum.android.diploma.common.data.dto
/**
 *        Минимальная зарплата
 *        Максимальная зарплата
 *        Валюта
 *        До вычета налогов (true) или нетто (false)
 */

data class SalaryDto(
    val from: Int?,
    val to: Int?,
    val currency: CurrencyIds,
    val gross: Boolean
)


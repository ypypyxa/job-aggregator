package ru.practicum.android.diploma.common.data.dto

import com.google.gson.annotations.SerializedName

/**
 * ID вакансии
 * Название вакансии
 * Полное описание
 * Отдел компании
 * Требуемый опыт
 * Тип занятости
 * Регион
 * График работы
 * Краткая информация
 * Зарплата
 * Работодатель
 * Контакты
 * Ключевые навыки
 *
 */

data class VacancyItemDto(
    val id: Int,
    val name: String,
    val description: String?,
    val department: IdNameDto?,
    val experience: IdNameDto?,
    val employment: IdNameDto?,
    val area: IdNameDto?,
    val schedule: IdNameDto?,
    val snippet: SnippetDto?,
    val salary: SalaryDto?,
    val employer: EmployerDto?,
    val contacts: ContactsDto?,
    @SerializedName("key_skills") val keySkills: List<IdNameDto>?,
    @SerializedName("alternate_url") val alternateUrl: String,
)

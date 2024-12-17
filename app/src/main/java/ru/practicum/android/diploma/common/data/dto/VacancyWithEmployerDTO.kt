package ru.practicum.android.diploma.common.data.dto

/**
 * vacancyId:      Уникальный идентификатор вакансии.
 * title:          Название позиции, указанное в вакансии.
 * city:           Город, в котором предлагается работа.
 * jobDescription: Подробное описание обязанностей и требований.
 * experience:     Требуемый уровень опыта работы (например, "3-5 лет").
 * employment:     Тип занятости, такой как полная или частичная занятость.
 * keySkillsJSON:  Ключевые навыки, представленные в формате JSON.
 * contactName:    Имя контактного лица для данной вакансии.
 * contactEmail:   Электронная почта контактного лица.
 * phonesJSON:     Номера телефонов, представленные в формате JSON.
 * salaryFrom:     Минимальная предлагаемая зарплата.
 * salaryTo:       Максимальная предлагаемая зарплата.
 * currency:       Валюта, в которой указана зарплата (например, "RUR" для рублей).
 * schedule:       График работы (например, полный рабочий день).
 * lastUpdate:     Временная метка последнего обновления вакансии в формате Unix Time.
 * employerId:     Уникальный идентификатор компании-работодателя.
 * logosJSON:      Логотипы компании, представленные в формате JSON.
 * employerName:   Название компании-работодателя.
 * employerLogoUri:URI основного логотипа компании.
 */

data class VacancyWithEmployerDTO(
    val vacancyId: Int,
    val title: String,
    val city: String?,
    val jobDescription: String?,
    val experience: String?,
    val employment: String?,
    val keySkillsJSON: String?,
    val contactName: String?,
    val contactEmail: String?,
    val phonesJSON: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val currency: String?,
    val schedule: String?,
    val lastUpdate: Long,
    val employerId: Int,
    val logosJSON: String?,
    val employerName: String,
    val employerLogoUri: String?
)

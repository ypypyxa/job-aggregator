package ru.practicum.android.diploma.favorites.domain.api

import ru.practicum.android.diploma.common.data.db.entity.EmployerEntity
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.dto.VacancyWithEmployerDTO
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface FavoritesDBConverter {

    /**
     * Преобразование списка [VacancyWithEmployerDTO] в список [VacancySearch].
     *
     * @param from Список DTO вакансий с информацией о работодателе.
     * @return Список вакансий для отображения.
     */
    fun mapList(from: List<VacancyWithEmployerDTO>): List<VacancySearch>

    /**
     * Преобразование одного [VacancyWithEmployerDTO] в [VacancySearch].
     *
     * @param from DTO вакансии с информацией о работодателе.
     * @return Модель вакансии для отображения.
     */
    fun map(from: VacancyWithEmployerDTO): VacancyDetails

    /**
     * Преобразование [VacancyDetails] в [VacancyEntity] для сохранения в базу данных.
     *
     * @param from Модель вакансии с деталями.
     * @return Модель вакансии для базы данных.
     */
    fun mapToEntity(from: VacancyDetails): VacancyEntity

    /**
     * Преобразование [VacancyDetails] в [EmployerEntity] для сохранения в базу данных.
     *
     * @param from Модель вакансии с деталями.
     * @return Модель работодателя для базы данных или null.
     */
    fun mapToEmployerEntity(from: VacancyDetails): EmployerEntity?

    /**
     * Преобразование одного [VacancyWithEmployerDTO] в [VacancyDetails].
     *
     * @param from DTO вакансии с информацией о работодателе.
     * @return Модель вакансии с детальной информацией.
     */
    fun mapToDetails(from: VacancyWithEmployerDTO): VacancyDetails
}

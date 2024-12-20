package ru.practicum.android.diploma.favorites.domain.api

import ru.practicum.android.diploma.common.data.db.entity.EmployerEntity
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.dto.VacancyWithEmployerDTO
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface FavoritesDBConverter {
    fun map(from: List<VacancyWithEmployerDTO>, page: Int, found: Int, totalPages: Int): VacancySearch
    fun map(from: VacancyWithEmployerDTO): VacancyDetails
    fun mapToEntity(from: VacancyDetails): VacancyEntity
    fun mapToEmployerEntity(from: VacancyDetails): EmployerEntity?
}

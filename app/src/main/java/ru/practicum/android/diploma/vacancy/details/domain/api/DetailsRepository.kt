package ru.practicum.android.diploma.vacancy.details.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

interface DetailsRepository {
    fun fetchDetails(vacancyId: Int): Flow<Resource<VacancyDetails>>
}

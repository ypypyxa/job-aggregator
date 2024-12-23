package ru.practicum.android.diploma.vacancy.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.search.domain.model.PagedData
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface SearchRepository {
    fun fetchVacancies(params: Map<String, Any?>): Flow<Resource<PagedData<VacancySearch>>>
}

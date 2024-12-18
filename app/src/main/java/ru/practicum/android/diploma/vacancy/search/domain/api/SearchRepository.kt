package ru.practicum.android.diploma.vacancy.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface SearchRepository {
    fun fetchVacancies(
        text: String?,
        page: Int,
        perPage: Int,
        area: Int?,
        searchField: String?,
        industry: String?,
        salary: Int?,
        onlyWithSalary: Boolean
    ) : Flow<Resource<List<VacancySearch>>>
}

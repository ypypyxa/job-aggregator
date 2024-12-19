package ru.practicum.android.diploma.vacancy.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface SearchInteractor {
    fun fetchVacancies(params: Map<String, Any?>): Flow<Pair<List<VacancySearch>?, String?>>
}

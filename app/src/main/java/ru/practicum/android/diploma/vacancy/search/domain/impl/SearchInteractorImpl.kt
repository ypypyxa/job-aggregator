package ru.practicum.android.diploma.vacancy.search.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchRepository
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun fetchVacancies(params: Map<String, Any?>): Flow<Pair<List<VacancySearch>?, String?>> {
        return repository.fetchVacancies(params).map { result ->
            when (result) {
                is Resource.Success -> { Pair(result.data, null) }
                is Resource.Error -> { Pair(null, result.message) }
            }
        }
    }
}

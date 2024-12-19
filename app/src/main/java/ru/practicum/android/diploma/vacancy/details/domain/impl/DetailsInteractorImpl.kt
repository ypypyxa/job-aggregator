package ru.practicum.android.diploma.vacancy.details.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsInteractor
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsRepository
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

class DetailsInteractorImpl(private val repository: DetailsRepository) : DetailsInteractor {
    override fun fetchDetails(vacancyId: Int): Flow<Pair<VacancyDetails?, String?>> {
        return repository.fetchDetails(vacancyId).map { result ->
            when (result) {
                is Resource.Success -> { Pair(result.data, null) }
                is Resource.Error -> { Pair(null, result.message) }
            }
        }
    }
}


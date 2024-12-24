package ru.practicum.android.diploma.vacancy.filter.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class AreaInteractorImpl(private val repository: AreaRepository) : AreaInteractor {

    override fun fetchArea(): Flow<Pair<List<Area>?, String?>> {
        return repository.fetchArea().map { result ->
            when (result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
            }
        }
    }

    override fun fetchAreaById(areaId: String): Flow<Pair<List<Area>?, String?>> {
        return repository.fetchAreaById(areaId).map { result ->
            when (result) {
                is Resource.Success -> Pair(result.data, null)
                is Resource.Error -> Pair(null, result.message)
            }
        }
    }
}

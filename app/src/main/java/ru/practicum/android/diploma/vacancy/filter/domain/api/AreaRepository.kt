package ru.practicum.android.diploma.vacancy.filter.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

interface AreaRepository {
    fun fetchCountries(): Flow<Resource<List<Area>>>
    fun fetchAreaById(areaId: String): Flow<Resource<Area>>
}

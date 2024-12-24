package ru.practicum.android.diploma.vacancy.filter.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

interface AreaInteractor {
    fun fetchArea(): Flow<Pair<List<Area>?, String?>>
    fun fetchAreaById(areaId: String): Flow<Pair<List<Area>?, String?>>
}

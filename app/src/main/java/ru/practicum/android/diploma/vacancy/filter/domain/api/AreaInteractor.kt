package ru.practicum.android.diploma.vacancy.filter.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

interface AreaInteractor {
    fun fetchCountries(): Flow<Pair<List<Area>?, String?>>
    fun fetchRegions(countryId: String): Flow<Pair<List<Area>?, String?>>
    fun fetchCountryById(countryId: String): Flow<Pair<Area?, String?>>
    fun fetchRegionById(regionId: String): Flow<Pair<Area?, String?>>
}

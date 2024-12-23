package ru.practicum.android.diploma.common.data.impl

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.NO_INTERNET_ERROR
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.SUCCESS
import ru.practicum.android.diploma.common.data.network.requests.AreaRequest
import ru.practicum.android.diploma.common.data.network.response.AreaResponse
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class AreaRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context
) : AreaRepository {

    override fun fetchCountries(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(AreaRequest())
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val areas = (response as AreaResponse).areas.map { it.toDomain() }
                emit(Resource.success(areas))
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }

    override fun fetchRegions(countryId: String): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(AreaRequest(countryId.toInt()))
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val regions = (response as AreaResponse).areas.firstOrNull()?.areas?.map { it.toDomain() } ?: emptyList()
                emit(Resource.success(regions))
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }

    override fun fetchCountryById(countryId: String): Flow<Resource<Area>> = flow {
        val response = networkClient.doRequest(AreaRequest(countryId.toInt()))
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val country = (response as AreaResponse).areas.firstOrNull()?.toDomain()
                country?.let { emit(Resource.success(it)) }
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }

    override fun fetchRegionById(regionId: String): Flow<Resource<Area>> = flow {
        val response = networkClient.doRequest(AreaRequest(regionId.toInt()))
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val region = (response as AreaResponse).areas.firstOrNull()?.toDomain()
                region?.let { emit(Resource.success(it)) }
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }
}

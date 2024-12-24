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
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class AreaRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
    private val converter: Converter
) : AreaRepository {

    override fun fetchArea(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(AreaRequest())
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val areas = (response as AreaResponse).areas.map {
                    converter.AreaDtotoArea(it)
                }
                emit(Resource.success(areas))
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }

    override fun fetchAreaById(areaId: String): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(AreaRequest(areaId.toInt()))
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.error(NO_INTERNET_ERROR, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val regions = (response as AreaResponse).areas.firstOrNull()?.areas?.map {
                    converter.AreaDtotoArea(it)
                } ?: emptyList()
                emit(Resource.success(regions))
            }
            else -> {
                emit(Resource.error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }
}

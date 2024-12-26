package ru.practicum.android.diploma.common.data.impl

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.NO_INTERNET_ERROR
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.SUCCESS
import ru.practicum.android.diploma.common.data.network.requests.VacancyDetailRequest
import ru.practicum.android.diploma.common.data.network.response.VacancyDetailResponse
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsRepository
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

class DetailsRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
    private val converter: Converter
) : DetailsRepository {
    override fun fetchDetails(vacancyId: Int): Flow<Resource<VacancyDetails>> = flow {
        val response = networkClient.doRequest(
            VacancyDetailRequest(vacancyId)
        )
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                emit(Resource.Success(converter.convertToVacancyDetails(response as VacancyDetailResponse)))
            }
            else -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }
}

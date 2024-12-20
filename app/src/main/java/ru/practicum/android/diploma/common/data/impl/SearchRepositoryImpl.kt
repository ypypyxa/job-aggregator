package ru.practicum.android.diploma.common.data.impl

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.NO_INTERNET_ERROR
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient.Companion.SUCCESS
import ru.practicum.android.diploma.common.data.network.requests.SearchRequest
import ru.practicum.android.diploma.common.data.network.response.SearchResponse
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchRepository
import ru.practicum.android.diploma.vacancy.search.domain.model.PagedData
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
    private val converter: Converter
) : SearchRepository {
    override fun fetchVacancies(params: Map<String, Any?>): Flow<Resource<PagedData<VacancySearch>>> = flow {
        val response = networkClient.doRequest(
            SearchRequest(params)
        )
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                val searchResponse = response as SearchResponse
                val pagedData = PagedData(
                    items = converter.convertVacanciesSearch(searchResponse),
                    currentPage = searchResponse.page,
                    totalPages = searchResponse.pages,
                    totalItems = searchResponse.found
                )
                emit(Resource.Success(pagedData))
            }
            else -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }
}

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
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context,
    private val converter: Converter
) : SearchRepository {
    override fun fetchVacancies(
        text: String?,
        page: Int,
        perPage: Int,
        area: Int?,
        searchField: String?,
        industry: String?,
        salary: Int?,
        onlyWithSalary: Boolean
    ): Flow<Resource<List<VacancySearch>>> = flow {
        val response = networkClient.doRequest(SearchRequest(
            text = text,
            page = page,
            perPage = perPage,
            area = area,
            searchField = searchField,
            industry =  industry,
            salary = salary,
            onlyWithSalary = onlyWithSalary)
        )
        when (response.resultCode) {
            NO_INTERNET_ERROR -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.search_no_internet)))
            }
            SUCCESS -> {
                emit(Resource.Success(converter.convertVacanciesSearch(response as SearchResponse)))
            }
            else -> {
                emit(Resource.Error(response.resultCode, context.getString(R.string.server_error)))
            }
        }
    }
}

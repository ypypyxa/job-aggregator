package ru.practicum.android.diploma.common.data.impl

import android.util.Log
import retrofit2.HttpException
import ru.practicum.android.diploma.common.data.dto.IndustriesDto
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.requests.IndustryRequest
import ru.practicum.android.diploma.common.data.network.response.IndustryResponse
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import java.io.IOException

class IndustryFilterRepositoryImpl(
    private val networkClient: NetworkClient,
    private val converter: Converter

) : IndustryFilterRepository {

    private val industryCache = mutableListOf<IndustriesDto>()

    override suspend fun getIndustryFilterSettings(): List<FilterIndustryValue> {
        return industryCache.map { dto ->
            converter.industriesDtoToDomain(dto)
        }
    }

    override suspend fun fetchIndustries() {
        try {
            val response = networkClient.doRequest(IndustryRequest()) as IndustryResponse
            industryCache.clear()
            industryCache.addAll(response.industries)
        } catch (e: IOException) {
            Log.e("IndustryFilterRepository", "Network error: ${e.message}")
            throw e
        } catch (e: HttpException) {
            Log.e("IndustryFilterRepository", "HTTP error: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e("IndustryFilterRepository", "Unexpected error: ${e.localizedMessage}")
            throw e
        }
    }

}

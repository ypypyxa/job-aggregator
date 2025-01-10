package ru.practicum.android.diploma.common.data.impl

import android.content.Context
import android.util.Log
import retrofit2.HttpException
import ru.practicum.android.diploma.common.data.dto.IndustriesDto
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.requests.IndustryRequest
import ru.practicum.android.diploma.common.data.network.response.IndustryResponse
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.common.utils.isInternetAvailable
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import java.io.IOException

class IndustryFilterRepositoryImpl(
    private val context: Context,
    private val networkClient: NetworkClient,
    private val converter: Converter

) : IndustryFilterRepository {
    companion object {
        private const val LOG_TAG = "IndustryFilterRepository"
        private const val NETWORK_ERROR = "Network error: "
        private const val HTTP_ERROR = "HTTP error: "
        private const val CAST_ERROR = "Class cast error: "
        private const val STATE_ERROR = "Illegal state error: "
    }

    private val industryCache = mutableListOf<IndustriesDto>()

    override suspend fun getIndustryFilterSettings(): List<FilterIndustryValue> {
        return industryCache.map { dto ->
            converter.industriesDtoToDomain(dto)
        }
    }

    override suspend fun fetchIndustries() {
        if (!context.isInternetAvailable()) {
            throw IOException("No internet connection")
        }

        try {
            val response = networkClient.doRequest(IndustryRequest()) as IndustryResponse
            require(response.industries.isNotEmpty()) { "Industry list is empty" }

            industryCache.clear()
            response.industries.forEach { industry ->
                addIndustryRecursively(industry)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "$NETWORK_ERROR${e.message}")
            throw e
        } catch (e: HttpException) {
            Log.e(LOG_TAG, "$HTTP_ERROR${e.code()} - ${e.message()}")
            throw e
        } catch (e: ClassCastException) {
            Log.e(LOG_TAG, "$CAST_ERROR${e.message}")
            throw e
        } catch (e: IllegalStateException) {
            Log.e(LOG_TAG, "$STATE_ERROR${e.message}")
            throw e
        }
    }

    private fun addIndustryRecursively(industry: IndustriesDto) {
        industryCache.add(industry)
        industry.industries?.forEach { subIndustry ->
            addIndustryRecursively(subIndustry)
        }
    }
}

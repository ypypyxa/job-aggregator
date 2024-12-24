package ru.practicum.android.diploma.common.data.impl

import ru.practicum.android.diploma.common.data.dto.IndustriesDto
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class IndustryFilterRepositoryImpl(
    private val api: HeadHunterApi
) : IndustryFilterRepository {

    private val industryCache = mutableListOf<IndustriesDto>()

    override suspend fun getIndustryFilterSettings(): List<FilterIndustryValue> {
        return industryCache.map { it.toDomain() }
    }

    override suspend fun fetchIndustries() {
        try {
            val industries = api.getIndustries()
            industryCache.clear()
            industryCache.addAll(industries)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    // Преобразование IndustriesDto -> FilterIndustryValue
    private fun IndustriesDto.toDomain(): FilterIndustryValue {
        return FilterIndustryValue(
            id = this.id,
            text = this.name
        )
    }

}

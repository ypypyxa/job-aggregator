package ru.practicum.android.diploma.vacancy.filter.domain.api

import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

interface IndustryFilterRepository {
    suspend fun getIndustryFilterSettings(): List<FilterIndustryValue>
    suspend fun fetchIndustries()
}

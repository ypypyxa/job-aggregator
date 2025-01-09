package ru.practicum.android.diploma.vacancy.filter.domain.api

import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

interface IndustryFilterInteractor {
    suspend fun fetchIndustries(): List<FilterIndustryValue>
    suspend fun getCachedIndustries(): List<FilterIndustryValue>
    suspend fun getDomainIndustries(): List<FilterIndustryValue>
    fun saveSelectedIndustry(industry: FilterIndustryValue?)
    fun getSelectedIndustry(): FilterIndustryValue?
}

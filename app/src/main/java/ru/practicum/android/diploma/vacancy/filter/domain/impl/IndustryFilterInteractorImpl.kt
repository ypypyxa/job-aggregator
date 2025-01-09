package ru.practicum.android.diploma.vacancy.filter.domain.impl

import ru.practicum.android.diploma.vacancy.filter.data.IndustryLocalDataSource
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class IndustryFilterInteractorImpl(
    private val repository: IndustryFilterRepository,
    private val localDataSource: IndustryLocalDataSource
) : IndustryFilterInteractor {

    override suspend fun fetchIndustries(): List<FilterIndustryValue> {
        repository.fetchIndustries()
        return repository.getIndustryFilterSettings()
    }

    override suspend fun getCachedIndustries(): List<FilterIndustryValue> {
        return repository.getIndustryFilterSettings()
    }

    override suspend fun getDomainIndustries(): List<FilterIndustryValue> {
        return repository.getIndustryFilterSettings()
    }

    override fun saveSelectedIndustry(industry: FilterIndustryValue?) {
        localDataSource.saveIndustry(industry)
    }

    override fun getSelectedIndustry(): FilterIndustryValue? {
        return localDataSource.getSavedIndustry()
    }
}

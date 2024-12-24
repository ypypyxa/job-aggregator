package ru.practicum.android.diploma.vacancy.filter.domain.impl

import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class IndustryFilterInteractorImpl(
    private val repository: IndustryFilterRepository
) : IndustryFilterInteractor {

    // Запрос к API для получения отраслей и обновления кеша
    override suspend fun fetchIndustries(): List<FilterIndustryValue> {
        repository.fetchIndustries()
        return repository.getIndustryFilterSettings()
    }

    // Получение кэшированных отраслей без обращения к API
    override suspend fun getCachedIndustries(): List<FilterIndustryValue> {
        return repository.getIndustryFilterSettings()
    }

    override suspend fun getDomainIndustries(): List<FilterIndustryValue> {
        return repository.getIndustryFilterSettings() // Преобразуем в Domain модель
    }
}

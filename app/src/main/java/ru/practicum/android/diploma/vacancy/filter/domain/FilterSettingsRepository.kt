package ru.practicum.android.diploma.vacancy.filter.domain

import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings

interface FilterSettingsRepository {
    fun getFilterSettings(): FilterSettings
    fun saveFilterSettings(filterSettings: FilterSettings)
    fun clearFilterSettings()
    fun clearRegionAndCountry()
}

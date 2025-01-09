package ru.practicum.android.diploma.vacancy.filter.domain

import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings

class FilterSettingsInteractor(private val filterSettingsRepository: FilterSettingsRepository) {

    // Получить настройки фильтров
    fun getFilterSettings(): FilterSettings {
        return filterSettingsRepository.getFilterSettings()
    }

    // Сохранить настройки фильтров
    fun saveFilterSettings(filterSettings: FilterSettings) {
        filterSettingsRepository.saveFilterSettings(filterSettings)
    }

    // Очистить настройки фильтров
    fun clearFilterSettings() {
        filterSettingsRepository.clearFilterSettings()
    }
}

package ru.practicum.android.diploma.vacancy.filter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings

class FilterViewModel(
    private val filterSettingsInteractor: FilterSettingsInteractor

) : ViewModel() {

    private val _selectedIndustry = MutableStateFlow<FilterIndustryValue?>(null)
    val selectedIndustry: StateFlow<FilterIndustryValue?> = _selectedIndustry

    private val _onlyWithSalary = MutableStateFlow(false)
    val onlyWithSalary: StateFlow<Boolean> = _onlyWithSalary

    private val _filterSettings = MutableStateFlow<FilterSettings?>(null)
    val filterSettings: StateFlow<FilterSettings?> = _filterSettings


    // Получить настройки фильтров
    fun loadFilterSettings() {
        viewModelScope.launch {
            val settings = filterSettingsInteractor.getFilterSettings()
            _filterSettings.value = settings
        }
    }

    // Сохранить настройки фильтров
    fun saveFilterSettings(settings: FilterSettings) {
        viewModelScope.launch {
            filterSettingsInteractor.saveFilterSettings(settings)
            _filterSettings.update { settings }
        }
    }

    // Очистить настройки фильтров
    fun clearFilterSettings() {
        viewModelScope.launch {
            filterSettingsInteractor.clearFilterSettings()
            _filterSettings.value = null
        }

    }
}

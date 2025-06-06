package ru.practicum.android.diploma.vacancy.filter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings

class FilterViewModel(
    private val interactor: IndustryFilterInteractor,
    private val filterSettingsInteractor: FilterSettingsInteractor

) : ViewModel() {

    private var _previousFilterSettings: FilterSettings? = null

    private val _selectedIndustry = MutableStateFlow<FilterIndustryValue?>(null)
    val selectedIndustry: StateFlow<FilterIndustryValue?> = _selectedIndustry

    private val _onlyWithSalary = MutableStateFlow(false)
    val onlyWithSalary: StateFlow<Boolean> = _onlyWithSalary

    private val _filterSettings = MutableStateFlow<FilterSettings?>(null)
    val filterSettings: StateFlow<FilterSettings?> = _filterSettings

    init {
        loadSelectedIndustry()
    }

    // Загрузка сохраненной отрасли
    private fun loadSelectedIndustry() {
        _selectedIndustry.value = interactor.getSelectedIndustry()
    }

    // Сохранение выбранной отрасли
    fun saveIndustry(industry: FilterIndustryValue?) {
        interactor.saveSelectedIndustry(industry)
        _selectedIndustry.value = industry
    }
    fun setOnlyWithSalary(value: Boolean) {
        _onlyWithSalary.value = value
    }

    // Получить настройки фильтров
    fun loadFilterSettings() {
        viewModelScope.launch {
            val settings = filterSettingsInteractor.getFilterSettings()
            _filterSettings.value = settings
            _previousFilterSettings = settings
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

    fun hasFilterChanged(newSettings: FilterSettings): Boolean {
        val result = _previousFilterSettings != newSettings
        return result
    }

    fun onClearWorkplacePressed() {
        val newSettings = FilterSettings(
            null,
            null,
            _filterSettings.value?.industry,
            _filterSettings.value?.expectedSalary ?: -1,
            _filterSettings.value?.notShowWithoutSalary ?: false
        )

        DataTransmitter.apply {
            postRegion(null)
            postCountry(null)
        }

        _filterSettings.update { newSettings }
    }

    fun onClearIndustryPressed() {
        val newSettings = FilterSettings(
            _filterSettings.value?.country,
            _filterSettings.value?.region,
            null,
            _filterSettings.value?.expectedSalary ?: -1,
            _filterSettings.value?.notShowWithoutSalary ?: false
        )

        _filterSettings.update { newSettings }
    }
}

package ru.practicum.android.diploma.vacancy.filter.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class FilterViewModel(
    private val interactor: IndustryFilterInteractor
) : ViewModel() {

    private val _selectedIndustry = MutableStateFlow<FilterIndustryValue?>(null)
    val selectedIndustry: StateFlow<FilterIndustryValue?> = _selectedIndustry

    init {
        loadSelectedIndustry()
    }

    // Загрузка сохраненной отрасли
    private fun loadSelectedIndustry() {
        _selectedIndustry.value = interactor.getSelectedIndustry()
    }

    // Сохранение выбранной отрасли
    fun saveIndustry(industry: FilterIndustryValue) {
        interactor.saveSelectedIndustry(industry)
        _selectedIndustry.value = industry
    }
}

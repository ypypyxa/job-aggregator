package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class ChooseIndustryViewModel(
    private val interactor: IndustryFilterInteractor
) : ViewModel() {

    private val _industryState = MutableStateFlow<List<FilterIndustryValue>>(emptyList())
    val industryState: StateFlow<List<FilterIndustryValue>> = _industryState

    init {
        fetchIndustries()
    }

    private fun fetchIndustries() {
        viewModelScope.launch {
            try {
                val industries = interactor.fetchIndustries()
                _industryState.value = industries
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

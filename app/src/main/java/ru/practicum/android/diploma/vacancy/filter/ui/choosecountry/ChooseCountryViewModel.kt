package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class ChooseCountryViewModel(
    private val areaInteractor: AreaInteractor,
    private val context: Context
) : ViewModel() {


    fun loadVacancy() {
        viewModelScope.launch {
            areaInteractor.fetchArea()
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }

        }
    }

    fun loadVacancyById(areaId: String) {
        viewModelScope.launch {
            areaInteractor.fetchAreaById(areaId)
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }

        }
    }

    private fun processResult(areaResult: List<Area>?, errorMessage: String?) {
        var area = areaResult
        when {
            errorMessage != null -> {
                Log.d("ChooseArea", "$errorMessage")
            }
            area == null -> {
                Log.d("ChooseArea", "Такого места не существует")
            }
            else -> {
                Log.d("ChooseArea", "$area")
            }
        }
    }
}

package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class ChooseRegionViewModel(
    private val areaInteractor: AreaInteractor
) : ViewModel() {

    private fun loadAreaById(areaId: String) {
        viewModelScope.launch {
            areaInteractor.fetchAreaById(areaId)
                .collect { resource ->
                    areaResult(resource.first, resource.second)
                }
        }
    }

    private fun areaResult(areaResult: Area?, errorMessage: String?) {
        var area = areaResult
        when {
            errorMessage != null -> {
                Log.d(CHOOSE_AREA, "$errorMessage")
            }

            area == null -> {
                Log.d(CHOOSE_AREA, "Такого места не существует")
            }

            else -> {
                Log.d(CHOOSE_AREA, "$area")
            }
        }
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
    }
}

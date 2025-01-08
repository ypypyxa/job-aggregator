package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.choosecountry.model.ChooseCountryFragmentState

class ChooseCountryViewModel(
    private val areaInteractor: AreaInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseCountryFragmentState>()
    fun observeState(): LiveData<ChooseCountryFragmentState> = mediatorStateLiveData

    init {
        viewModelScope.launch {
            loadCountries()
        }
    }

    private val mediatorStateLiveData = MediatorLiveData<ChooseCountryFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseCountryFragmentState.Content -> ChooseCountryFragmentState.Content(state.areas)
                is ChooseCountryFragmentState.Loading -> ChooseCountryFragmentState.Loading
                else -> { null }
            }
        }
    }

    private fun loadCountries() {
        renderState(ChooseCountryFragmentState.Loading)
        viewModelScope.launch {
            areaInteractor.fetchCountries()
                .collect { resource ->
                    countriesResult(resource.first, resource.second)
                }
        }
    }

    private fun countriesResult(areasResult: List<Area>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                Log.d(CHOOSE_AREA, "$errorMessage")
            }
            areasResult == null -> {
                Log.d(CHOOSE_AREA, "Такого места не существует")
            }
            else -> {
                Log.d(CHOOSE_AREA, "$areasResult")
                val areas = areasResult.sortArea()
                renderState(ChooseCountryFragmentState.Content(areas))
            }
        }
    }

    private fun List<Area>.sortArea(): List<Area> {
        return sortedWith { area1, area2 ->
            when {
                area1.id == "1001" && area2.id != "1001" -> 1
                area1.id != "1001" && area2.id == "1001" -> -1
                else -> 0
            }
        }
    }

    private fun renderState(state: ChooseCountryFragmentState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
    }
}

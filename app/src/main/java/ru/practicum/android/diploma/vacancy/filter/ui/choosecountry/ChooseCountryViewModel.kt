package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
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
            val areas = getDefaultCountryList()
            renderState(ChooseCountryFragmentState.Default(areas))
        }
    }

    private val mediatorStateLiveData = MediatorLiveData<ChooseCountryFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseCountryFragmentState.Default -> ChooseCountryFragmentState.Default(state.areas)
                is ChooseCountryFragmentState.Content -> ChooseCountryFragmentState.Content(state.areas)
                else -> { null }
            }
        }
    }

    fun loadCountries() {
        viewModelScope.launch {
            areaInteractor.fetchCountries()
                .collect { resource ->
                    countriesResult(resource.first, resource.second)
                }
        }
    }

    private fun countriesResult(areasResult: List<Area>?, errorMessage: String?) {
        var areas = areasResult
        when {
            errorMessage != null -> {
                Log.d(CHOOSE_AREA, "$errorMessage")
            }
            areas == null -> {
                Log.d(CHOOSE_AREA, "Такого места не существует")
            }
            else -> {
                Log.d(CHOOSE_AREA, "$areas")
                renderState(ChooseCountryFragmentState.Content(areas))
            }
        }
    }

    private fun renderState(state: ChooseCountryFragmentState) {
        stateLiveData.postValue(state)
    }

    private suspend fun getDefaultCountryList(): List<Area> {
        val areaIds = listOf(RUSSIA, UKRAINE, KAZAKHSTAN, AZERBAIJAN, BELARUS, GEORGIA, KYRGYZSTAN, UZBEKISTAN)

        val areas = areaIds.mapNotNull { areaId ->
            areaInteractor.fetchAreaById(areaId).firstOrNull()?.first
        }.toMutableList() // Преобразуем в изменяемый список для добавления

        areas.add(Area(id = "-1", name = "Другие регионы", parentId = null, parentName = null, areas = emptyList()))

        return areas
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
        private const val RUSSIA = "113"
        private const val UKRAINE = "5"
        private const val KAZAKHSTAN = "40"
        private const val AZERBAIJAN = "9"
        private const val BELARUS = "16"
        private const val GEORGIA = "28"
        private const val KYRGYZSTAN = "48"
        private const val UZBEKISTAN = "97"
    }
}

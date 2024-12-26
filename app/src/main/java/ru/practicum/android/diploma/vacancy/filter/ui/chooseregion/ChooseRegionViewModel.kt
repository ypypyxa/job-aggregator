package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model.ChooseRegionFragmentState

class ChooseRegionViewModel(
    private val areaInteractor: AreaInteractor,
    private val countryId: String
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseRegionFragmentState>()
    fun observeState(): LiveData<ChooseRegionFragmentState> = mediatorStateLiveData

    init {
        loadAreaById(countryId)
    }

    private var countryName: String? = null

    private val mediatorStateLiveData = MediatorLiveData<ChooseRegionFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseRegionFragmentState.ShowRegion ->
                    ChooseRegionFragmentState.ShowRegion(state.areas, state.countryName)
                is ChooseRegionFragmentState.ShowCity ->
                    ChooseRegionFragmentState.ShowCity(state.areas)
                else -> null
            }
        }
    }

    fun loadAreaById(areaId: String?) {
        viewModelScope.launch {
            areaInteractor.fetchAreaById(areaId!!)
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
                countryName = area.name
                renderState(ChooseRegionFragmentState.ShowRegion(area.areas, area.name))
            }
        }
    }

    fun loadCityByAreaId(areaId: String?) {
        viewModelScope.launch {
            areaInteractor.fetchAreaById(areaId!!)
                .collect { resource ->
                    cityResult(resource.first, resource.second)
                }
        }
    }
    private fun cityResult(areaResult: Area?, errorMessage: String?) {
        var area = areaResult
        when {
            errorMessage != null -> {
                Log.d(CHOOSE_AREA, "$errorMessage")
            }

            area == null -> {
                Log.d(CHOOSE_AREA, "Такого места не существует")
            }

            else -> {
                renderState(ChooseRegionFragmentState.ShowCity(area.areas))
            }
        }
    }

    private fun renderState(state: ChooseRegionFragmentState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
    }
}

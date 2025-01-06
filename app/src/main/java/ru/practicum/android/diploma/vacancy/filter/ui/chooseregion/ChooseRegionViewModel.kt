package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model.ChooseRegionFragmentState

class ChooseRegionViewModel(
    private val areaInteractor: AreaInteractor,
    private val countryId: String?
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseRegionFragmentState>()
    fun observeState(): LiveData<ChooseRegionFragmentState> = mediatorStateLiveData

    init {
        loadAreaById(countryId)
    }

    private var countryName: String? = null
    private var selectedCountry: Area? = null

    private val mediatorStateLiveData = MediatorLiveData<ChooseRegionFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseRegionFragmentState.ShowRegion ->
                    ChooseRegionFragmentState.ShowRegion(state.areas, state.countryName)
                is ChooseRegionFragmentState.ShowCity ->
                    ChooseRegionFragmentState.ShowCity(state.areas)
                is ChooseRegionFragmentState.ShowSearch ->
                    ChooseRegionFragmentState.ShowSearch(state.areas)
                is ChooseRegionFragmentState.NothingFound ->
                    ChooseRegionFragmentState.NothingFound
                else -> ChooseRegionFragmentState.ShowError
            }
        }
    }

    fun loadAreaById(areaId: String?) {
        Log.d("ChooseRegionViewModel", "areaId: $areaId")
        viewModelScope.launch {
            if (areaId.isNullOrEmpty()) {
                // Запрашиваем список всех стран
                areaInteractor.fetchCountries()
                    .collect { pair ->
                        val countries = pair.first
                        val errorMessage = pair.second

                        if (!countries.isNullOrEmpty()) {
                            val allRegions = mutableListOf<Area>()

                            // Для каждой страны запрашиваем регионы и города
                            countries.forEach { country ->
                                areaInteractor.fetchAreaById(country.id)
                                    .collect { areaPair ->
                                        val region = areaPair.first
                                        val regionError = areaPair.second

                                        if (region != null) {
                                            allRegions.addAll(region.areas)
                                        } else {
                                            Log.e(CHOOSE_AREA, "Error fetching areas for country ${country.id}: $regionError")
                                        }
                                    }
                            }

                            // Отображаем все регионы и города
                            renderState(ChooseRegionFragmentState.ShowRegion(allRegions, "Все регионы"))
                        } else {
                            Log.e(CHOOSE_AREA, "Error fetching countries: $errorMessage")
                            renderState(ChooseRegionFragmentState.ShowError)
                        }
                    }
            } else {
                // Если страна выбрана, загружаем её регионы
                areaInteractor.fetchAreaById(areaId)
                    .collect { areaPair ->
                        val area = areaPair.first
                        val errorMessage = areaPair.second

                        if (area != null) {
                            areaResult(Resource.success(area))
                        } else {
                            areaResult(Resource.error(-1, errorMessage))
                        }
                    }
            }
        }
    }







    private fun areaResult(resource: Resource<Area>) {
        when (resource) {
            is Resource.Success -> {
                val area = resource.data
                if (area == null) {
                    renderState(ChooseRegionFragmentState.ShowError)
                    Log.d(CHOOSE_AREA, "Такого места не существует")
                } else {
                    selectedCountry = area
                    countryName = area.name
                    renderState(ChooseRegionFragmentState.ShowRegion(area.areas, area.name))
                }
            }
            is Resource.Error -> {
                renderState(ChooseRegionFragmentState.ShowError)
                Log.d(CHOOSE_AREA, resource.message ?: "Произошла ошибка")
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

    fun searchArea(query: String) {
        if (query.isBlank() || selectedCountry == null) {
            renderState(
                ChooseRegionFragmentState.ShowRegion(selectedCountry?.areas, selectedCountry?.name)
            )
            return
        }

        val lowerCaseQuery = query.lowercase()
        val nameMatches = mutableListOf<Area>()
        val parentNameMatches = mutableListOf<Area>()

        fun searchRecursively(area: Area) {
            // Проверяем совпадение с name
            if (area.name.lowercase().startsWith(lowerCaseQuery)) {
                nameMatches.add(area)
            }
            // Проверяем совпадение с parentName
            else if (area.parentName?.lowercase()?.startsWith(lowerCaseQuery) == true) {
                parentNameMatches.add(area)
            }
            // Рекурсивно ищем в вложенных areas
            area.areas.forEach { subArea ->
                searchRecursively(subArea)
            }
        }

        selectedCountry!!.areas.forEach { area ->
            searchRecursively(area)
        }

        // Объединяем результаты: сначала совпадения по name, затем по parentName
        val searchedArea = nameMatches + parentNameMatches

        if (searchedArea.isEmpty()) {
            renderState(ChooseRegionFragmentState.NothingFound)
        } else {
            renderState(ChooseRegionFragmentState.ShowSearch(searchedArea))
        }
    }

    fun onClearSearch() {
        renderState(ChooseRegionFragmentState.ShowRegion(selectedCountry?.areas, selectedCountry?.name))
    }

    private fun renderState(state: ChooseRegionFragmentState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
    }
}

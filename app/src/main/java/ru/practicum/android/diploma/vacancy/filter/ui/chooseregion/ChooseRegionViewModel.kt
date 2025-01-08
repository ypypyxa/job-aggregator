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

    private val areaCache = mutableMapOf<String, Area>()

    private var foundingAreas = emptyList<Area>()

    private val mediatorStateLiveData = MediatorLiveData<ChooseRegionFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseRegionFragmentState.ShowRegions ->
                    ChooseRegionFragmentState.ShowRegions(state.areas)
                is ChooseRegionFragmentState.ShowSearch ->
                    ChooseRegionFragmentState.ShowSearch(state.areas)
                is ChooseRegionFragmentState.NothingFound ->
                    ChooseRegionFragmentState.NothingFound
                is ChooseRegionFragmentState.Loading -> ChooseRegionFragmentState.Loading
                else -> ChooseRegionFragmentState.ShowError
            }
        }
    }
    init {
        // Проверяем кэш перед загрузкой данных
        loadAreaById(countryId)
    }

    fun loadAreaById(areaId: String?) {
        Log.d("ChooseRegionViewModel", "areaId: $areaId")

        renderState(ChooseRegionFragmentState.Loading)

        areaId?.let { id ->
            areaCache[id]?.let { cachedArea ->
                renderState(ChooseRegionFragmentState.ShowRegions(cachedArea.areas))
                return
            }
        }

        if (areaId.isNullOrEmpty()) {
            loadCountries()
        } else {
            loadAreaForId(areaId)
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            areaInteractor.fetchCountries()
                .collect { pair ->
                    val countries = pair.first
                    val errorMessage = pair.second

                    if (!countries.isNullOrEmpty()) {
                        loadRegionsForCountries(countries)
                    } else {
                        Log.e(CHOOSE_AREA, "Error fetching countries: $errorMessage")
                        renderState(ChooseRegionFragmentState.ShowError)
                    }
                }
        }
    }

    private fun loadRegionsForCountries(countries: List<Area>) {
        viewModelScope.launch {
            val allRegions = mutableListOf<Area>()

            countries.forEach { country ->
                areaInteractor.fetchAreaById(country.id)
                    .collect { areaPair ->
                        val region = areaPair.first
                        val regionError = areaPair.second

                        if (region != null) {
                            allRegions.addAll(region.areas)
                            // Кэшируем данные
                            areaCache[country.id] = region
                        } else {
                            Log.e(CHOOSE_AREA, "Error fetching areas for country ${country.id}: $regionError")
                        }
                    }
            }
            foundingAreas = allRegions
            renderState(ChooseRegionFragmentState.ShowRegions(allRegions))
        }
    }

    private fun loadAreaForId(areaId: String) {
        viewModelScope.launch {
            areaInteractor.fetchAreaById(areaId)
                .collect { areaPair ->
                    val area = areaPair.first
                    val errorMessage = areaPair.second

                    if (area != null) {
                        areaCache[areaId] = area
                        areaResult(Resource.success(area))
                    } else {
                        areaResult(Resource.error(-1, errorMessage))
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
                    foundingAreas = area.areas
                    renderState(ChooseRegionFragmentState.ShowRegions(foundingAreas))
                }
            }
            is Resource.Error -> {
                renderState(ChooseRegionFragmentState.ShowError)
                Log.d(CHOOSE_AREA, resource.message ?: "Произошла ошибка")
            }
        }
    }

    fun searchArea(query: String) {
        if (query.isBlank() || foundingAreas == null) {
            renderState(
                ChooseRegionFragmentState.ShowRegions(foundingAreas)
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

        foundingAreas.forEach { area ->
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
        renderState(ChooseRegionFragmentState.ShowRegions(foundingAreas))
    }

    private fun renderState(state: ChooseRegionFragmentState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val CHOOSE_AREA = "ChooseArea"
    }
}

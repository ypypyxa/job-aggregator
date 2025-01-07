package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model.ChooseWorkplaceFragmentState

class ChooseWorkplaceViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseWorkplaceFragmentState>()
    fun observeState(): LiveData<ChooseWorkplaceFragmentState> = mediatorStateLiveData

    init {
        val country = DataTransmitter.getCountry()
        val region = DataTransmitter.getRegion()

        if (country == null && region == null) {
            renderState(ChooseWorkplaceFragmentState.Empty)
        } else if (region != null) {
            val regionArea = Area(region.id, region.name, country?.id, country?.name, emptyList())
            renderState(ChooseWorkplaceFragmentState.CitySelected(regionArea))
        } else if (country != null) {
            val countryArea = Area(country.id, country.name, null, null, emptyList())
            renderState(ChooseWorkplaceFragmentState.CountrySelected(countryArea))
        }
    }

    private val mediatorStateLiveData = MediatorLiveData<ChooseWorkplaceFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseWorkplaceFragmentState.Empty -> ChooseWorkplaceFragmentState.Empty
                is ChooseWorkplaceFragmentState.CountrySelected ->
                    ChooseWorkplaceFragmentState.CountrySelected(state.area)
                is ChooseWorkplaceFragmentState.CitySelected ->
                    ChooseWorkplaceFragmentState.CitySelected(state.area)
            }
        }
    }

    private fun renderState(state: ChooseWorkplaceFragmentState) {
        stateLiveData.postValue(state)
    }
    private fun setCountry(area: Area) {
        if (!area.name.isNullOrEmpty()) {
            renderState(ChooseWorkplaceFragmentState.CountrySelected(area))
        } else {
            renderState(ChooseWorkplaceFragmentState.Empty)
        }
    }

    private fun setCity(area: Area) {
        if (!area.name.isNullOrEmpty()) {
            renderState(ChooseWorkplaceFragmentState.CitySelected(area))
        } else {
            renderState(ChooseWorkplaceFragmentState.Empty)
        }
    }

    fun setContent(area: Area) {
        if (area.parentId.isNullOrEmpty()) {
            setCountry(area)
        } else {
            setCity(area)
        }
    }
}

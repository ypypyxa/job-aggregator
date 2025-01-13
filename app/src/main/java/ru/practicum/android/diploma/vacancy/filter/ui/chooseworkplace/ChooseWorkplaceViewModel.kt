package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model.ChooseWorkplaceFragmentState

class ChooseWorkplaceViewModel(
    private val filterSettingsInteractor: FilterSettingsInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseWorkplaceFragmentState>()
    fun observeState(): LiveData<ChooseWorkplaceFragmentState> = mediatorStateLiveData

    init {
        val country = DataTransmitter.getCountry()
        val region = DataTransmitter.getRegion()

        if (region?.id.isNullOrEmpty()) {
            if (country?.id.isNullOrEmpty()) {
                renderState(ChooseWorkplaceFragmentState.Empty)
            } else {
                val countryArea = Area(country!!.id, country.name, null, null, emptyList())
                renderState(ChooseWorkplaceFragmentState.CountrySelected(countryArea))
            }
        } else {
            val regionArea = Area(region!!.id, region.name, country?.id, country?.name, emptyList())
            renderState(ChooseWorkplaceFragmentState.RegionSelected(regionArea))
        }
    }

    private val mediatorStateLiveData = MediatorLiveData<ChooseWorkplaceFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseWorkplaceFragmentState.Empty -> ChooseWorkplaceFragmentState.Empty
                is ChooseWorkplaceFragmentState.CountrySelected ->
                    ChooseWorkplaceFragmentState.CountrySelected(state.area)
                is ChooseWorkplaceFragmentState.RegionSelected ->
                    ChooseWorkplaceFragmentState.RegionSelected(state.area)
            }
        }
    }

    private fun renderState(state: ChooseWorkplaceFragmentState) {
        stateLiveData.postValue(state)
    }
    private fun setCountry(area: Area) {
        if (area.name.isNotEmpty()) {
            renderState(ChooseWorkplaceFragmentState.CountrySelected(area))
        } else {
            renderState(ChooseWorkplaceFragmentState.Empty)
        }
    }

    private fun setCity(area: Area) {
        if (area.name.isNotEmpty()) {
            renderState(ChooseWorkplaceFragmentState.RegionSelected(area))
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

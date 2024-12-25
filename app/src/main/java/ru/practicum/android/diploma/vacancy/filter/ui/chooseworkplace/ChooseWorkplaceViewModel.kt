package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model.ChooseWorkplaceFragmentState

class ChooseWorkplaceViewModel(
    private var countryName: String
) : ViewModel() {

    private val stateLiveData = MutableLiveData<ChooseWorkplaceFragmentState>()
    fun observeState(): LiveData<ChooseWorkplaceFragmentState> = mediatorStateLiveData

    init {
        when (countryName) {
            "Unknown" -> renderState(ChooseWorkplaceFragmentState.Empty)
            else -> renderState(ChooseWorkplaceFragmentState.CountrySelected(countryName))
        }
    }

    private val mediatorStateLiveData = MediatorLiveData<ChooseWorkplaceFragmentState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is ChooseWorkplaceFragmentState.Empty -> ChooseWorkplaceFragmentState.Empty
                is ChooseWorkplaceFragmentState.CountrySelected ->
                    ChooseWorkplaceFragmentState.CountrySelected(state.name)
                is ChooseWorkplaceFragmentState.CitySelected -> ChooseWorkplaceFragmentState.CitySelected(state.name)
            }
        }
    }

    private fun renderState(state: ChooseWorkplaceFragmentState) {
        stateLiveData.postValue(state)
    }
}

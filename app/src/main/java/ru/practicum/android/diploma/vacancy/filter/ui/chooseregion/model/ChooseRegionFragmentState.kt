package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model

import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

interface ChooseRegionFragmentState {

    data class ShowRegion(val areas: List<Area>?, val countryName: String?) : ChooseRegionFragmentState

    data class ShowCity(val areas: List<Area>) : ChooseRegionFragmentState

    data class ShowSearch(val areas: List<Area>?) : ChooseRegionFragmentState
}

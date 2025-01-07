package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model

import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

interface ChooseRegionFragmentState {

    data class ShowRegions(val areas: List<Area>?) : ChooseRegionFragmentState

    data class ShowSearch(val areas: List<Area>?) : ChooseRegionFragmentState

    object NothingFound : ChooseRegionFragmentState

    object ShowError : ChooseRegionFragmentState
}

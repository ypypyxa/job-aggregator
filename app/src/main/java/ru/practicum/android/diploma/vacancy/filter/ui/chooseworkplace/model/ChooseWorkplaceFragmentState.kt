package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model

import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

sealed interface ChooseWorkplaceFragmentState {
    object Empty : ChooseWorkplaceFragmentState

    data class CountrySelected(val area: Area) : ChooseWorkplaceFragmentState
    data class RegionSelected(val area: Area) : ChooseWorkplaceFragmentState
}

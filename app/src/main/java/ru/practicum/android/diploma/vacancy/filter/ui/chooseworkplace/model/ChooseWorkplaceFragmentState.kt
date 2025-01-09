package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model

sealed interface ChooseWorkplaceFragmentState {
    object Empty : ChooseWorkplaceFragmentState

    data class CountrySelected(val name: String?) : ChooseWorkplaceFragmentState
    data class CitySelected(val countryName: String?, val cityName: String?) : ChooseWorkplaceFragmentState
}

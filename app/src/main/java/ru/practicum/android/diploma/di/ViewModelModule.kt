package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.ui.FavoritesViewModel
import ru.practicum.android.diploma.vacancy.details.ui.DetailsViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.FilterViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.choosecountry.ChooseCountryViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry.ChooseIndustryViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.ChooseRegionViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.ChooseWorkplaceViewModel
import ru.practicum.android.diploma.vacancy.search.ui.SearchViewModel

val viewModelModule = module {
    viewModelOf(::SearchViewModel)
    viewModelOf(::FilterViewModel)
    viewModelOf(::FavoritesViewModel)

    viewModel { (vacancyId: Int) ->
        DetailsViewModel(get(), get(), androidContext(), vacancyId)
    }
    viewModel { (countryName: String, cityName: String) ->
        ChooseWorkplaceViewModel(countryName, cityName)
    }
    viewModel { ChooseCountryViewModel(get()) }

    viewModel { (countryId: String?) ->
        ChooseRegionViewModel(get(), countryId)
    }

    viewModelOf(::ChooseCountryViewModel)
    viewModelOf(::ChooseIndustryViewModel)
}

package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.ui.FavoritesViewModel
import ru.practicum.android.diploma.vacancy.details.ui.DetailsViewModel
import ru.practicum.android.diploma.vacancy.filter.ui.FilterViewModel
import ru.practicum.android.diploma.vacancy.search.ui.SearchViewModel

val viewModelModule = module {
    viewModel { SearchViewModel() }
    viewModel { FavoritesViewModel() }
    viewModel { FilterViewModel() }
    viewModel { DetailsViewModel() }
}

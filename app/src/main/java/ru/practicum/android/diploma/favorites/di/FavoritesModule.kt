package ru.practicum.android.diploma.favorites.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.ui.FavoritesViewModel

val favoritesModule = module {
    viewModel { FavoritesViewModel() }
}

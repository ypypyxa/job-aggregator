package ru.practicum.android.diploma.vacancy.search.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.vacancy.search.ui.SearchViewModel

val vacancySearchModule = module {
    viewModel { SearchViewModel() }
}

package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsInteractor
import ru.practicum.android.diploma.vacancy.details.domain.impl.DetailsInteractorImpl
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.impl.SearchInteractorImpl

val intercatorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }
    single<DetailsInteractor> {
        DetailsInteractorImpl(get())
    }
}

package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.favorites.domain.impl.FavoritesInteractorImpl
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsInteractor
import ru.practicum.android.diploma.vacancy.details.domain.impl.DetailsInteractorImpl
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterInteractor
import ru.practicum.android.diploma.vacancy.filter.domain.impl.AreaInteractorImpl
import ru.practicum.android.diploma.vacancy.filter.domain.impl.IndustryFilterInteractorImpl
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.impl.SearchInteractorImpl

val intercatorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }
    single<DetailsInteractor> {
        DetailsInteractorImpl(get())
    }
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get()) }
    single<AreaInteractor> {
        AreaInteractorImpl(get()) }

    factory<IndustryFilterInteractor> {
        IndustryFilterInteractorImpl(get(), get())
    }

}

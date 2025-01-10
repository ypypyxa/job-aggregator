package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.common.data.impl.AreaRepositoryImpl
import ru.practicum.android.diploma.common.data.impl.DetailsRepositoryImpl
import ru.practicum.android.diploma.common.data.impl.FavoritesRepositoryImpl
import ru.practicum.android.diploma.common.data.impl.IndustryFilterRepositoryImpl
import ru.practicum.android.diploma.common.data.impl.SearchRepositoryImpl
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.vacancy.details.domain.api.DetailsRepository
import ru.practicum.android.diploma.vacancy.filter.data.FilterSettingsRepositoryImpl
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsRepository
import ru.practicum.android.diploma.vacancy.filter.domain.api.AreaRepository
import ru.practicum.android.diploma.vacancy.filter.domain.api.IndustryFilterRepository
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchRepository

val repositoryModule = module {

    factory { Converter() }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), androidContext(), get())
    }
    single<DetailsRepository> {
        DetailsRepositoryImpl(get(), androidContext(), get())
    }
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
    single<AreaRepository> {
        AreaRepositoryImpl(get(), androidContext(), get())
    }
    single<IndustryFilterRepository> {
        IndustryFilterRepositoryImpl(androidContext(),get(), get())
    }
    single<FilterSettingsRepository> {
        FilterSettingsRepositoryImpl(get(), get())
    }

}

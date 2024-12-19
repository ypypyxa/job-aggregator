package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.common.data.impl.SearchRepositoryImpl
import ru.practicum.android.diploma.common.utils.Converter
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchRepository

val repositoryModule = module {

    factory { Converter() }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), androidContext(), get())
    }
}

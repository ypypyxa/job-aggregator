package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.vacancy.search.domain.VacancyRepository

val repositoryModule = module {

    single { VacancyRepository(get()) }
}

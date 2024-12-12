package ru.practicum.android.diploma.app.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.app.di.appModule
import ru.practicum.android.diploma.common.di.dataModule
import ru.practicum.android.diploma.favorites.di.favoritesModule
import ru.practicum.android.diploma.vacancy.filter.di.vacancyFilterModule
import ru.practicum.android.diploma.vacancy.search.di.vacancySearchModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule, dataModule, favoritesModule, vacancyFilterModule, vacancySearchModule)
        }
    }
}

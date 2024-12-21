package ru.practicum.android.diploma.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.common.data.db.AppDatabase
import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.favorites.domain.api.FavoritesDBConverter
import ru.practicum.android.diploma.favorites.domain.impl.FavoritesDBConverterImpl

val dataModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.hh.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(HeadHunterApi::class.java) }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    // Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // DAO
    single { get<AppDatabase>().employerDao() }
    single { get<AppDatabase>().vacancyDao() }
    single { get<AppDatabase>().vacancyEmployerReferenceDao() }

    single<FavoritesDBConverter> { FavoritesDBConverterImpl(get()) }
    single { com.google.gson.Gson() }
}

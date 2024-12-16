package ru.practicum.android.diploma.di

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.common.data.network.HeadHunterApi

val dataModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.hh.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    single { get<Retrofit>().create(HeadHunterApi::class.java) }


}

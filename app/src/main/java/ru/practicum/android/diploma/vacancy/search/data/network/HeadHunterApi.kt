package ru.practicum.android.diploma.vacancy.search.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface HeadHunterApi {

    @GET("vacancies")
    suspend fun getVacancies(
        @Query("text") text: String,
        @Query("area") area: String?,
        @Query("industry") industry: String?,
        @Query("salary") salary: Int?
    ): VacanciesResponse
}

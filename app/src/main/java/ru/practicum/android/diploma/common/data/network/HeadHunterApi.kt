package ru.practicum.android.diploma.common.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.practicum.android.diploma.common.data.network.response.VacanciesResponse

interface HeadHunterApi {

    @GET("vacancies")
    suspend fun getVacancies(
        @Query("text") text: String,
        @Query("area") area: String?,
        @Query("industry") industry: String?,
        @Query("salary") salary: Int?
    ): VacanciesResponse
}

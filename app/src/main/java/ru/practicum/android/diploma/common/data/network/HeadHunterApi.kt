package ru.practicum.android.diploma.common.data.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.common.data.dto.AreasDto
import ru.practicum.android.diploma.common.data.dto.IndustriesDto
import ru.practicum.android.diploma.common.data.dto.VacancyItemDto
import ru.practicum.android.diploma.common.data.network.response.SearchResponse

interface HeadHunterApi {

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: Practicum HH Client/1.0 (zod15ru@gmail.com)"
    )
    @GET("vacancies")
    suspend fun getVacancies(
        @Query("text") text: String? = null,
        @Query("page") page: Int = 0,
        @Query("per_page") perPage: Int = 20,
        @Query("area") area: Int? = null,
        @Query("search_field") searchField: String? = "name",
        @Query("industry") industry: String? = null,
        @Query("salary") salary: Int? = null,
        @Query("only_with_salary") onlyWithSalary: Boolean = false
    ): SearchResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: Practicum HH Client/1.0 (zod15ru@gmail.com)"
    )
    @GET("vacancies/{vacancy_id}")
    suspend fun getVacancy(
        @Path("vacancy_id") vacancyId: Int
    ): VacancyItemDto

    @GET("areas")
    suspend fun getAreas(): List<AreasDto>

    @GET("industries")
    suspend fun getIndustries(): List<IndustriesDto>

    @GET("areas/{area_id}")
    suspend fun getAreaById(
        @Path("area_id") areaId: Int
    ): AreasDto
}

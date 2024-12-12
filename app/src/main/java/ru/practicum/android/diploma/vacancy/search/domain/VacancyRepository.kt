package ru.practicum.android.diploma.vacancy.search.domain

import ru.practicum.android.diploma.vacancy.search.data.network.HeadHunterApi
import ru.practicum.android.diploma.vacancy.search.data.network.NetworkClient
import ru.practicum.android.diploma.vacancy.search.data.network.VacanciesResponse

class VacancyRepository {

    private val api = NetworkClient.retrofit.create(HeadHunterApi::class.java)

    suspend fun fetchVacancies(
        text: String,
        area: String?,
        industry: String?,
        salary: Int?
    ): VacanciesResponse {
        return api.getVacancies(text, area, industry, salary)
    }
}

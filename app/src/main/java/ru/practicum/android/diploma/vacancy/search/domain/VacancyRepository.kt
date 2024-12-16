package ru.practicum.android.diploma.vacancy.search.domain

import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.NetworkClient
import ru.practicum.android.diploma.common.data.network.response.VacanciesResponse

class VacancyRepository( private val api: HeadHunterApi) {



    suspend fun fetchVacancies(
        text: String,
        area: String?,
        industry: String?,
        salary: Int?
    ): VacanciesResponse {
        return api.getVacancies(text, area, industry, salary)
    }
}

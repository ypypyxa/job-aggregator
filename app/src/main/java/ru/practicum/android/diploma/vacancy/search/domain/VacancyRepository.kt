package ru.practicum.android.diploma.vacancy.search.domain

import ru.practicum.android.diploma.common.data.network.HeadHunterApi
import ru.practicum.android.diploma.common.data.network.response.SearchResponse

class    VacancyRepository(private val api: HeadHunterApi) {
    suspend fun fetchVacancies(
        text: String? = null,
        page: Int = 0,
        perPage: Int = 20,
        area: Int? = null,
        searchField: String? = "name",
        industry: String? = null,
        salary: Int? = null,
        onlyWithSalary: Boolean = false
    ): SearchResponse {
        return api.getVacancies(
            text = text,
            page = page,
            perPage = perPage,
            area = area,
            searchField = searchField,
            industry = industry,
            salary = salary,
            onlyWithSalary = onlyWithSalary
        )
    }
}

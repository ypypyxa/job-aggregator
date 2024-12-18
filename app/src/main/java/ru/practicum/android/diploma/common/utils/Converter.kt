package ru.practicum.android.diploma.common.utils

import ru.practicum.android.diploma.common.data.network.response.SearchResponse
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class Converter {
    fun convertVacanciesSearch(response: SearchResponse): List<VacancySearch> {
        return response.items.map { vacancy ->
            VacancySearch(
                id = vacancy.id,
                name = vacancy.name,
                address = vacancy.area?.name,
                company = vacancy.employer?.name,
                salary = vacancy.salary?.toString(),
                logo = ""
            )
        }
    }
}

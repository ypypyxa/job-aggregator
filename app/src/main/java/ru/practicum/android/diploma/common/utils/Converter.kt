package ru.practicum.android.diploma.common.utils

import com.google.gson.Gson
import ru.practicum.android.diploma.common.data.network.response.SearchResponse
import ru.practicum.android.diploma.common.data.network.response.VacancyDetailResponse
import ru.practicum.android.diploma.vacancy.details.domain.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class Converter {
    private val gson = Gson()

    fun convertVacanciesSearch(response: SearchResponse): List<VacancySearch> {
        return response.items.map { vacancy ->
            VacancySearch(
                id = vacancy.id,
                name = vacancy.name,
                address = vacancy.area?.name,
                company = vacancy.employer?.name,
                salary = vacancy.salary?.toString(),
                logo = vacancy.employer?.logoUrls?.original
            )
        }
    }

    fun convertToVacancyDetails(response: VacancyDetailResponse): VacancyDetails {
        return VacancyDetails(
            vacancyId = response.vacancy.id,
            title = response.vacancy.name,
            city = response.vacancy.area?.name,
            jobDescription = response.vacancy.description,
            experience = response.vacancy.experience?.name,
            employment = response.vacancy.employment?.name,
            keySkillsJSON = response.vacancy.keySkills?.let { gson.toJson(it) },
            contactName = response.vacancy.contacts?.name,
            contactEmail = response.vacancy.contacts?.email,
            phonesJSON = response.vacancy.contacts?.phones?.let { gson.toJson(it) },
            salaryFrom = response.vacancy.salary?.from,
            salaryTo = response.vacancy.salary?.to,
            currency = response.vacancy.salary?.currency?.name,
            schedule = response.vacancy.schedule?.name,
            employerId = response.vacancy.employer?.id ?: 0,
            logosJSON = response.vacancy.employer?.logoUrls?.let { gson.toJson(it) },
            employerName = response.vacancy.employer?.name ?: "Unknown",
            employerLogoUri = response.vacancy.employer?.logoUrls?.original
        )
    }
}

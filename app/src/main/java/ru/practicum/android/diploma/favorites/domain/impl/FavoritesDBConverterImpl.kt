package ru.practicum.android.diploma.favorites.domain.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.common.data.db.entity.EmployerEntity
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.dto.ContactsDto
import ru.practicum.android.diploma.common.data.dto.PhoneDto
import ru.practicum.android.diploma.common.data.dto.VacancyWithEmployerDTO
import ru.practicum.android.diploma.favorites.domain.api.FavoritesDBConverter
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class FavoritesDBConverterImpl(
    private val gsonService: Gson
) : FavoritesDBConverter {

    override fun map(
        from: List<VacancyWithEmployerDTO>,
        page: Int,
        found: Int,
        totalPages: Int
    ): VacancySearch =
        VacancySearch(
            id = page,
            name = "Vacancies Page $page",
            address = null,
            company = null,
            salary = null,
            logo = null
        )

    override fun map(from: VacancyWithEmployerDTO): VacancyDetails {
        val logoMap = parseJson<Map<String, String>>(from.logosJSON)
        val phones = parseJson<ContactsDto>(from.phonesJSON)

        return VacancyDetails(
            vacancyId = from.vacancyId,
            title = from.title,
            city = from.city,
            jobDescription = from.jobDescription,
            experience = from.experience,
            employment = from.employment,
            keySkillsJSON = from.keySkillsJSON,
            contactName = from.contactName,
            contactEmail = from.contactEmail,
            phonesJSON = from.phonesJSON,
            salaryFrom = from.salaryFrom,
            salaryTo = from.salaryTo,
            currency = from.currency,
            schedule = from.schedule,
            employerId = from.employerId,
            logosJSON = from.logosJSON,
            employerName = from.employerName,
            employerLogoUri = logoMap?.get(LOGO_SIZE)
        )
    }

    override fun mapToEntity(from: VacancyDetails): VacancyEntity {
        return VacancyEntity(
            id = from.vacancyId,
            title = from.title,
            city = from.city,
            jobDescription = from.jobDescription,
            experience = from.experience,
            employment = from.employment,
            keySkillsJSON = from.keySkillsJSON,
            contactName = from.contactName,
            contactEmail = from.contactEmail,
            phonesJSON = from.phonesJSON,
            salaryFrom = from.salaryFrom,
            salaryTo = from.salaryTo,
            currency = from.currency,
            schedule = from.schedule,
            lastUpdate = System.currentTimeMillis()
        )
    }

    override fun mapToEmployerEntity(from: VacancyDetails): EmployerEntity? {
        return if (from.employerName != null) {
            EmployerEntity(
                id = from.employerId,
                logosJSON = from.logosJSON,
                employerName = from.employerName,
                employerLogoUri = from.employerLogoUri
            )
        } else {
            null
        }
    }

    private fun phoneDtoToPhone(from: PhoneDto): Pair<String, String> {
        return Pair(
            from.comment ?: "",
            listOfNotNull(from.country, from.city, from.number).joinToString(" ")
        )
    }

    private inline fun <reified T> parseJson(json: String?): T? {
        return try {
            json?.let { gsonService.fromJson(it, object : TypeToken<T>() {}.type) }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val LOGO_SIZE = "240"
    }
}

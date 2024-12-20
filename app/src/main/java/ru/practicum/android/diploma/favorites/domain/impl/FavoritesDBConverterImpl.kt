package ru.practicum.android.diploma.favorites.domain.impl

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.common.data.db.entity.EmployerEntity
import ru.practicum.android.diploma.common.data.db.entity.VacancyEntity
import ru.practicum.android.diploma.common.data.dto.VacancyWithEmployerDTO
import ru.practicum.android.diploma.favorites.domain.api.FavoritesDBConverter
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import java.io.IOException

class FavoritesDBConverterImpl(
    private val gsonService: Gson
) : FavoritesDBConverter {

    // Преобразование списка DTO в список VacancySearch
    override fun mapList(from: List<VacancyWithEmployerDTO>): List<VacancySearch> {
        return from.map { dto -> mapToSearch(dto) }
    }

    // Преобразование одного DTO в VacancySearch
    private fun mapToSearch(from: VacancyWithEmployerDTO): VacancySearch {
        return VacancySearch(
            id = from.vacancyId,
            name = from.title,
            address = from.city,
            company = from.employerName,
            salary = formatSalary(from.salaryFrom, from.salaryTo, from.currency),
            logo = from.employerLogoUri
        )
    }

    // Преобразование одного DTO в VacancyDetails
    override fun map(from: VacancyWithEmployerDTO): VacancyDetails {
        return mapToDetails(from)
    }

    // Преобразование VacancyDetails в VacancyEntity для базы данных
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

    // Преобразование VacancyDetails в EmployerEntity для базы данных
    override fun mapToEmployerEntity(from: VacancyDetails): EmployerEntity {
        return EmployerEntity(
            id = from.employerId,
            logosJSON = from.logosJSON,
            employerName = from.employerName,
            employerLogoUri = from.employerLogoUri
        )
    }

    // Преобразование DTO в VacancyDetails
    override fun mapToDetails(from: VacancyWithEmployerDTO): VacancyDetails {
        val logoMap = parseJson<Map<String, String>>(from.logosJSON)

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

    // Универсальный метод для парсинга JSON
    private inline fun <reified T> parseJson(json: String?): T? {
        return try {
            json?.let { gsonService.fromJson(it, object : TypeToken<T>() {}.type) }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "$JSON_SYNTAX_ERROR: $json", e)
            null
        } catch (e: JsonParseException) {
            Log.e(TAG, "$JSON_PARSE_ERROR: $json", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "$IO_ERROR: $json", e)
            null
        } catch (e: IllegalStateException) {
            // Gson может выбросить это исключение в случае неожиданного состояния
            Log.e(TAG, "Unexpected JSON structure or state: $json", e)
            null
        }
    }

    // Форматирование зарплаты
    private fun formatSalary(salaryFrom: Int?, salaryTo: Int?, currency: String?): String? {
        if (salaryFrom == null && salaryTo == null) return null

        val formattedSalary = when {
            salaryFrom != null && salaryTo != null -> "$salaryFrom-$salaryTo"
            salaryFrom != null -> "от $salaryFrom"
            salaryTo != null -> "до $salaryTo"
            else -> null
        }
        return "$formattedSalary $currency"
    }

    companion object {
        private const val LOGO_SIZE = "240"
        private const val TAG = "FavoritesDBConverter"
        private const val JSON_SYNTAX_ERROR = "Ошибка синтаксиса JSON"
        private const val JSON_PARSE_ERROR = "Ошибка парсинга JSON"
        private const val IO_ERROR = "Ошибка ввода-вывода при чтении JSON"
        private const val UNKNOWN_ERROR = "Неизвестная ошибка при парсинге JSON"
    }
}

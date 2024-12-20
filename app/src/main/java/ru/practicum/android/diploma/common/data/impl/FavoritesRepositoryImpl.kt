package ru.practicum.android.diploma.common.data.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.common.data.db.AppDatabase
import ru.practicum.android.diploma.favorites.domain.api.FavoritesDBConverter
import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class FavoritesRepositoryImpl(
    private val database: AppDatabase,
    private val converter: FavoritesDBConverter
) : FavoritesRepository {

    override suspend fun getFavoriteVacancies(page: Int, limit: Int): Flow<VacancySearch> {
        val total = withContext(Dispatchers.IO) { database.vacancyDao().getVacanciesCount() }
        val totalPages = (total.toDouble() / limit).toInt().coerceAtLeast(1)
        return withContext(Dispatchers.IO) {
            database.vacancyEmployerReferenceDao()
                .getAllVacancies(page, limit)
                .map { converter.map(it, page, total, totalPages) }
        }
    }

    override suspend fun isVacancyFavorite(vacancyId: Int): Boolean =
        withContext(Dispatchers.IO) { database.vacancyDao().isVacancyExists(vacancyId) }

    override suspend fun getFavoriteVacancy(vacancyId: Int): Flow<VacancyDetails?> {
        return withContext(Dispatchers.IO) {
            database.vacancyEmployerReferenceDao()
                .getVacancyWithEmployer(vacancyId)
                .map { it?.let(converter::map) }
        }
    }

    override suspend fun addFavoriteVacancy(vacancy: VacancyDetails) {
        withContext(Dispatchers.IO) {
            database.vacancyEmployerReferenceDao().addVacancy(
                vacancy = converter.mapToEntity(vacancy),
                employer = converter.mapToEmployerEntity(vacancy)
            )
        }
    }

    override suspend fun removeFavoriteVacancy(vacancyId: Int) {
        withContext(Dispatchers.IO) {
            database.vacancyDao().removeVacancyById(vacancyId)
        }
    }
}

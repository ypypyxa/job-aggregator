package ru.practicum.android.diploma.favorites.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface FavoritesRepository {
    suspend fun getFavoriteVacancies(page: Int, limit: Int): Flow<VacancySearch>
    suspend fun isVacancyFavorite(vacancyId: Int): Boolean
    suspend fun getFavoriteVacancy(vacancyId: Int): Flow<VacancyDetails?>
    suspend fun addFavoriteVacancy(vacancy: VacancyDetails)
    suspend fun removeFavoriteVacancy(vacancyId: Int)
}

package ru.practicum.android.diploma.favorites.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun getFavoriteVacancies(page: Int, limit: Int): Flow<List<VacancySearch>> {
        return repository.getFavoriteVacancies(page, limit)
    }

    override suspend fun isVacancyFavorite(vacancyId: Int): Boolean {
        return repository.isVacancyFavorite(vacancyId)
    }

    override suspend fun getFavoriteVacancy(vacancyId: Int): Flow<VacancyDetails?> {
        return repository.getFavoriteVacancy(vacancyId)
    }

    override suspend fun addFavoriteVacancy(vacancy: VacancyDetails) {
        repository.addFavoriteVacancy(vacancy)
    }

    override suspend fun removeFavoriteVacancy(vacancyId: Int) {
        repository.removeFavoriteVacancy(vacancyId)
    }
}

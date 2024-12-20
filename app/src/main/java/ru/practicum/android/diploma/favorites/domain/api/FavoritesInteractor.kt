package ru.practicum.android.diploma.favorites.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

interface FavoritesInteractor {

    /**ч
     * Получить список избранных вакансий с пагинацией.
     *
     * @param page Номер страницы.
     * @param limit Количество вакансий на странице.
     * @return Поток [VacancySearch] с информацией о найденных вакансиях.
     */
    suspend fun getFavoriteVacancies(page: Int, limit: Int): Flow<VacancySearch>

    /**
     * Проверить, является ли вакансия избранной.
     *
     * @param vacancyId Идентификатор вакансии.
     * @return `true`, если вакансия находится в списке избранных, иначе `false`.
     */
    suspend fun isVacancyFavorite(vacancyId: Int): Boolean

    /**
     * Получить детальную информацию об избранной вакансии.
     *
     * @param vacancyId Идентификатор вакансии.
     * @return Поток [DetailedVacancyItem] с детальной информацией о вакансии.
     */
    suspend fun getFavoriteVacancy(vacancyId: Int): Flow<VacancyDetails?>

    /**
     * Добавить вакансию в список избранных.
     *
     * @param vacancy Объект [DetailedVacancyItem] для добавления.
     */
    suspend fun addFavoriteVacancy(vacancy: VacancyDetails)

    /**
     * Удалить вакансию из списка избранных.
     *
     * @param vacancyId Идентификатор вакансии.
     */
    suspend fun removeFavoriteVacancy(vacancyId: Int)
}

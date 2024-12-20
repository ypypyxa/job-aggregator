package ru.practicum.android.diploma.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favoriteVacancies = MutableStateFlow<List<VacancySearch>>(emptyList())
    val favoriteVacancies: StateFlow<List<VacancySearch>> = _favoriteVacancies

    private val _isFavorite = MutableStateFlow<Boolean?>(null)
    val isFavorite: StateFlow<Boolean?> = _isFavorite

    private val _vacancyDetails = MutableStateFlow<VacancyDetails?>(null)
    val vacancyDetails: StateFlow<VacancyDetails?> = _vacancyDetails

    /**
     * Загрузить список избранных вакансий.
     *
     * @param page Номер страницы.
     * @param limit Количество элементов на странице.
     */
    fun loadFavoriteVacancies(page: Int, limit: Int) {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(page, limit).collect { vacancies ->
                _favoriteVacancies.value = vacancies // Исправление: метод возвращает `List<VacancySearch>`
            }
        }
    }

    /**
     * Проверить, является ли вакансия избранной.
     *
     * @param vacancyId Идентификатор вакансии.
     */
    fun checkIfFavorite(vacancyId: Int) {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isVacancyFavorite(vacancyId)
            _isFavorite.value = isFavorite
        }
    }

    /**
     * Получить детальную информацию о вакансии.
     *
     * @param vacancyId Идентификатор вакансии.
     */
    fun loadVacancyDetails(vacancyId: Int) {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancy(vacancyId).collect { details ->
                _vacancyDetails.value = details
            }
        }
    }

    /**
     * Добавить вакансию в избранное.
     *
     * @param vacancy Детальная информация о вакансии.
     */
    fun addToFavorites(vacancy: VacancyDetails) {
        viewModelScope.launch {
            favoritesInteractor.addFavoriteVacancy(vacancy)
            _isFavorite.value = true
            refreshFavorites()
        }
    }

    /**
     * Удалить вакансию из избранного.
     *
     * @param vacancyId Идентификатор вакансии.
     */
    fun removeFromFavorites(vacancyId: Int) {
        viewModelScope.launch {
            favoritesInteractor.removeFavoriteVacancy(vacancyId)
            _isFavorite.value = false
            refreshFavorites()
        }
    }

    /**
     * Обновить список избранных вакансий.
     */
    private fun refreshFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(0, 20).collect { vacancies ->
                _favoriteVacancies.value = vacancies
            }
        }
    }
}

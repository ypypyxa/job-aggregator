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

    private val _vacancyDetails = MutableStateFlow<VacancyDetails?>(null)
    val vacancyDetails: StateFlow<VacancyDetails?> = _vacancyDetails

    var isOfflineMode = false

    /**
     * Загрузить список избранных вакансий.
     *
     * @param page Номер страницы.
     * @param limit Количество элементов на странице.
     */
    fun loadFavoriteVacancies(page: Int, limit: Int) {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(page, limit).collect { vacancies ->
                _favoriteVacancies.value = vacancies
                isOfflineMode = vacancies.isEmpty()
            }
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
     * Обновить список избранных вакансий.
     */
    private fun refreshFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(DEFAULT_PAGE, DEFAULT_LIMIT).collect { vacancies ->
                _favoriteVacancies.value = vacancies
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_LIMIT = 20
    }
}

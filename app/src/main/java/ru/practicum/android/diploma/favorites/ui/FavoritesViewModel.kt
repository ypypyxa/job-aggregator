package ru.practicum.android.diploma.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favoriteVacancies = MutableStateFlow<List<VacancySearch>>(emptyList())
    val favoriteVacancies: StateFlow<List<VacancySearch>> = _favoriteVacancies

    private var isOfflineMode = false
    var hasLoadedBefore = false

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

                if (vacancies.isNotEmpty()) {
                    hasLoadedBefore = true
                }
            }
        }
    }

    /**
     * Обновить список избранных вакансий.
     */
    fun refreshFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(DEFAULT_PAGE, DEFAULT_LIMIT).collect { vacancies ->
                _favoriteVacancies.value = vacancies

                if (vacancies.isEmpty()) {
                    isOfflineMode = true
                    hasLoadedBefore = false
                }
            }
        }
    }
    fun loadFavoriteVacanciesOffline() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteVacancies(DEFAULT_PAGE, DEFAULT_LIMIT).collect { vacancies ->
                _favoriteVacancies.value = vacancies

                // Устанавливаем hasLoadedBefore, если в локальной базе есть вакансии
                if (vacancies.isNotEmpty()) {
                    hasLoadedBefore = true
                }
                isOfflineMode = vacancies.isEmpty()
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_LIMIT = 20
    }
}

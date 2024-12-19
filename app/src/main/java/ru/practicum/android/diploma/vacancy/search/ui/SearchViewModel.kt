package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearchParams

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    companion object {
        private const val LOADING_DELAY_MS = 2000L
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _vacancies = MutableLiveData<List<VacancySearch>>()
    val vacancies: LiveData<List<VacancySearch>> get() = _vacancies

    private var latestSearchText: String? = null

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = LOADING_DELAY_MS,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        searchRequest(query)
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            latestSearchText = null
            clearVacancies()
            return
        }

        if (latestSearchText != query) {
            latestSearchText = query
            debounceSearch(query)
        }
    }

    fun clearVacancies() {
        latestSearchText = null
        _vacancies.postValue(emptyList())
    }

    private fun searchRequest(query: String) {
        if (query.isBlank() || latestSearchText != query) {
            _isLoading.value = false
            return
        }

        _isLoading.postValue(true)

        //собираем параметры запроса
        val params = VacancySearchParams(
            text = latestSearchText,
            page = 0,
            perPage = 20,
            area = 1,
            searchField = "name",
            industry = null,
            salary = null,
            onlyWithSalary = false
        )

        //выполняем запрос
        viewModelScope.launch {
            searchInteractor.fetchVacancies(params.toQueryMap())
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }
        }

        _isLoading.postValue(false)
    }

    private fun processResult(foundVacancies: List<VacancySearch>?, errorMessage: String?) {
        val vacancies = mutableListOf<VacancySearch>()
        if (foundVacancies != null) {
            vacancies.addAll(foundVacancies)
        }
        when {
            errorMessage != null -> {
                Log.d("ErrorMessage", errorMessage)
            }
            vacancies.isEmpty() -> {
                Log.d("ErrorMessage", "Вакансий не найдено")
            }
            else -> {
                _vacancies.value = vacancies
                Log.d("SearchResult", vacancies.toString())
            }
        }
    }
}

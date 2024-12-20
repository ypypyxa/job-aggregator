package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.vacancy.search.domain.model.PagedData
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
    private var currentPage = 0
    private var totalPages = 1
    private val pageSize = 20

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = LOADING_DELAY_MS,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        resetSearch(query)
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            clearVacancies()
            return
        }

        if (latestSearchText != query) {
            latestSearchText = query
            debounceSearch(query)
        }
    }

    private fun resetSearch(query: String) {
        currentPage = 0
        totalPages = 1
        latestSearchText = query
        _vacancies.value = emptyList()
        searchRequest(query)
    }

    fun clearVacancies() {
        latestSearchText = null
        _vacancies.postValue(emptyList())
    }

    fun searchRequest(query: String? = latestSearchText) {
        if (_isLoading.value == true || query.isNullOrEmpty() || currentPage >= totalPages) return

        _isLoading.postValue(true)

        // собираем параметры запроса
        val params = VacancySearchParams(
            text = query,
            page = currentPage,
            perPage = pageSize,
            area = 1,
            searchField = "name",
            industry = null,
            salary = null,
            onlyWithSalary = false
        )

        // выполняем запрос
        viewModelScope.launch {
            searchInteractor.fetchVacancies(params.toQueryMap())
                .collect { resource ->
                    processResult(resource.first, resource.second)
                }
        }

        _isLoading.postValue(false)
    }

    private fun processResult(pagedData: PagedData<VacancySearch>?, errorMessage: String?) {
        if (pagedData != null) {
            val currentVacancies = _vacancies.value.orEmpty().toMutableList()
            currentVacancies.addAll(pagedData.items)

            _vacancies.value = currentVacancies
            currentPage = pagedData.currentPage + 1
            totalPages = pagedData.totalPages
        }

        if (errorMessage != null) {
            Log.e("SearchViewModel", "Error: $errorMessage")
        }
    }
}

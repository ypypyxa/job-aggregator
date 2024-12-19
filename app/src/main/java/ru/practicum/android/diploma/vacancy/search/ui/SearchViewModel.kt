package ru.practicum.android.diploma.vacancy.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.Resource
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.search.domain.api.SearchRepository
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    companion object {
        private const val LOADING_DELAY_MS = 2000L
        private const val PER_PAGE = 20
        private const val QUERY_PARAM = "query"
        private const val PER_PAGE_PARAM = "perPage"
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
        viewModelScope.launch {
            repository.fetchVacancies(mapOf(QUERY_PARAM to query, PER_PAGE_PARAM to PER_PAGE))
                .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _vacancies.value = result.data.orEmpty()
                    }
                    is Resource.Error -> {
                        _vacancies.value = emptyList()
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}

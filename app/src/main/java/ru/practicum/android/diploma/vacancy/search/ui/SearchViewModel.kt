package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
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
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _vacancies = MutableLiveData<List<VacancySearch>>()
    val vacancies: LiveData<List<VacancySearch>> get() = _vacancies

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var latestSearchText: String? = null

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = 2000L,
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
            repository.fetchVacancies(mapOf("query" to query, "perPage" to 20)).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _vacancies.value = result.data.orEmpty()
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _error.value = result.message ?: "Unknown error"
                        _vacancies.value = emptyList()
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

}

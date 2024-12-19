package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.data.dto.VacancyItemDto
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.search.domain.VacancyRepository

class SearchViewModel(
    private val repository: VacancyRepository
) : ViewModel() {

    companion object {
        private const val LOADING_DELAY_MS = 2000L
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _vacancies = MutableLiveData<List<VacancyItemDto>>()
    val vacancies: LiveData<List<VacancyItemDto>> get() = _vacancies

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
        if (latestSearchText != query) {
            latestSearchText = query
            debounceSearch(query)
        }
    }

    fun searchVacancies(query: String) {
        if (query.isBlank()) {
            _vacancies.postValue(emptyList())
            return
        }

        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val response = repository.fetchVacancies(text = query, perPage = 20)
                _vacancies.postValue(response.items)
            } catch (e: Exception) {
                _error.postValue(e.localizedMessage)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearVacancies() {
        _vacancies.postValue(emptyList())
    }

private fun searchRequest(query: String) {
    if (query.isBlank()) {
        _isLoading.value = false
        _vacancies.postValue(emptyList())
        return
    }
    _isLoading.postValue(true)
    viewModelScope.launch {
        delay(LOADING_DELAY_MS)
        if (latestSearchText == query && query.isNotBlank()) {
            _isLoading.postValue(false)
        }
    }
    searchVacancies(query)
}
}

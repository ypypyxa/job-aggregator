package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.debounce

class SearchViewModel() : ViewModel() {

    companion object {
        private const val LOADING_DELAY_MS = 2000L
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

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

    private fun searchRequest(query: String) {
        if (query.isBlank()) {
            _isLoading.value = false
            return
        }
        _isLoading.postValue(true)
        viewModelScope.launch {
            delay(LOADING_DELAY_MS)
            if (latestSearchText == query && query.isNotBlank()) {
                _isLoading.postValue(false)
            }
        }
    }

    fun loadVacancies() {
        viewModelScope.launch {}
    }
}

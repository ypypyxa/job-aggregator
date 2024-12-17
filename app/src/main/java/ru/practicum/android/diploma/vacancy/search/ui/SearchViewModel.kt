package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var latestSearchText: String? = null

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = 2000L,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        if (query.isNotBlank()) {
            _isLoading.postValue(true)
            viewModelScope.launch {
                delay(LOADING_DELAY_MS)
                _isLoading.postValue(false)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        if (latestSearchText != query) {
            latestSearchText = query
            if (query.isBlank()) {
                _isLoading.value = false
            } else {
                debounceSearch(query)
            }
        }
    }

    fun loadVacancies() {
        viewModelScope.launch {
            val response = repository.fetchVacancies(
                text = "Android developer",
                area = "1",
                industry = null,
                salary = null
            )
            // Логируем результат
            response.items.forEach { vacancy ->
                val logMessage = """
        Vacancy ID: ${vacancy.id}
        Name: ${vacancy.name}
        Region ID: ${vacancy.area.id}
        Region Name: ${vacancy.area.name}
        Salary: ${vacancy.salary?.from ?: "N/A"} - ${vacancy.salary?.to ?: "N/A"} ${vacancy.salary?.currency ?: "N/A"}
        Employer ID: ${vacancy.employer.id}
        Employer Name: ${vacancy.employer.name}
        Phone: ${vacancy.contacts?.phone ?: "Телефон не указан"}
        Email: ${vacancy.contacts?.email ?: "Email не указан"}
                """.trimIndent()
                Log.d("SearchViewModel", logMessage)
            }
        }
    }
}

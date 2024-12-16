package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.vacancy.search.domain.VacancyRepository

class SearchViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            _isLoading.value = false
            return
        }

        _isLoading.value = true
        debounceSearch(query)
    }

    private val debounceSearch: (String) -> Unit = debounce(
        delayMillis = 2000L,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) {
        _isLoading.postValue(false)
    }

    private val repository = VacancyRepository()

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

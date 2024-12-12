package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.search.domain.VacancyRepository

class SearchViewModel : ViewModel(){

    private val repository = VacancyRepository()

    fun loadVacancies() {
        viewModelScope.launch {
            try {
                val response = repository.fetchVacancies(
                    text = "Android developer",
                    area = "1",
                    industry = null,
                    salary = null
                )
                // Логируем результат
                Log.d("SearchViewModel", "Fetched Vacancies: $response")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching vacancies", e)
            }
        }
    }
}

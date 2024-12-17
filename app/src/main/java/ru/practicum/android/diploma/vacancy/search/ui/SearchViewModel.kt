package ru.practicum.android.diploma.vacancy.search.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.vacancy.search.domain.VacancyRepository

class SearchViewModel(
    private val repository: VacancyRepository
) : ViewModel() {

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

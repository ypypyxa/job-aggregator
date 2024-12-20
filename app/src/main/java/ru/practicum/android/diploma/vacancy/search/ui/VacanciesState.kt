package ru.practicum.android.diploma.vacancy.search.ui

import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

sealed interface VacanciesState {

    data class Content(val vacancies: List<VacancySearch>) : VacanciesState

    data class Error(val errorMessage: String) : VacanciesState

    data class Empty(val message: String?) : VacanciesState
}

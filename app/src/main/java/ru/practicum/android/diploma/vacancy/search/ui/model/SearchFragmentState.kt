package ru.practicum.android.diploma.vacancy.search.ui.model

import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch

sealed interface SearchFragmentState {

    object Default : SearchFragmentState

    object Loading : SearchFragmentState

    object Empty : SearchFragmentState

    object ServerError : SearchFragmentState

    object InternetError : SearchFragmentState

    data class Content(
        val vacancies: List<VacancySearch>,
        val vacanciesCount: Int
    ) : SearchFragmentState

    object UpdateList : SearchFragmentState
}

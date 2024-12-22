package ru.practicum.android.diploma.vacancy.details.ui.model

import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

interface DetailsFragmentState {

    object Loading : DetailsFragmentState

    object Empty : DetailsFragmentState

    object ServerError : DetailsFragmentState

    data class Content(val vacancy: VacancyDetails) : DetailsFragmentState

    data class OfflineContent(val vacancy: VacancyDetails?) : DetailsFragmentState
}

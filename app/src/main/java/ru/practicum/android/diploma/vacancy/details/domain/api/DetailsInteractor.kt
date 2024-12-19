package ru.practicum.android.diploma.vacancy.details.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

interface DetailsInteractor {
    fun fetchDetails(vacancyId: Int): Flow<Pair<VacancyDetails?, String?>>
}

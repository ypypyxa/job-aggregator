package ru.practicum.android.diploma.vacancy.details.domain.models

import ru.practicum.android.diploma.common.data.dto.PhoneDto

data class Contacts(
    val name: String?,
    val phones: List<PhoneDto>?,
    val email: String?
)

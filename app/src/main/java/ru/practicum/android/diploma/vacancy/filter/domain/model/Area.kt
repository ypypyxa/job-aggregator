package ru.practicum.android.diploma.vacancy.filter.domain.model

data class Area(
    val regionId : String,
    val regionName : String,
    val countryId : String?,
    val countryName : String?,
    val areas : List<Area>
)

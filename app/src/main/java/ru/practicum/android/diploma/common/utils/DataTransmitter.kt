package ru.practicum.android.diploma.common.utils

import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region

object DataTransmitter {

    private var currentIndustry: Industry? = null
    private var currentCountry: Country? = null
    private var currentRegion: Region? = null

    fun getRegion(): Region? = currentRegion

    fun getCountry(): Country? = currentCountry

    fun getIndustry(): Industry? = currentIndustry

    fun postRegion(region: Region?) {
        currentRegion = region
    }

    fun postCountry(country: Country?) {
        currentCountry = country
    }

    fun postIndustry(industry: Industry?) {
        currentIndustry = industry
    }
}

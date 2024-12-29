package ru.practicum.android.diploma.common.utils

import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region

object DataTransmitter {

    private var currentIndustry: Industry? = null
    private var currentCountry: Country? = null
    private var currentRegion: Region? = null

    // Получение текущего региона
    fun getRegion(): Region? {
        return currentRegion
    }

    // Получение текущей страны
    fun getCountry(): Country? {
        return currentCountry
    }

    // Получение текущей отрасли
    fun getIndustry(): Industry? {
        return currentIndustry
    }

    // Установка текущего региона
    fun postRegion(region: Region?) {
        currentRegion = region
    }

    // Установка текущей страны
    fun postCountry(country: Country?) {
        currentCountry = country
    }

    // Установка текущей отрасли
    fun postIndustry(industry: Industry?) {
        currentIndustry = industry
    }
}

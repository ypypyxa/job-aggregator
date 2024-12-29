package ru.practicum.android.diploma.common.utils

import android.util.Log
import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region

object DataTransmitter {

    private var currentIndustry: Industry? = null
    private var currentCountry: Country? = null
    private var currentRegion: Region? = null

    // Получение текущего региона
    fun getRegion(): Region? {
        Log.d("DataTransmitter", "Getting Region: $currentRegion")
        return currentRegion
    }

    // Получение текущей страны
    fun getCountry(): Country? {
        Log.d("DataTransmitter", "Getting Country: $currentCountry")
        return currentCountry
    }

    // Получение текущей отрасли
    fun getIndustry(): Industry? {
        Log.d("DataTransmitter", "Getting Industry: $currentIndustry")
        return currentIndustry
    }

    // Установка текущего региона
    fun postRegion(region: Region?) {
        Log.d("DataTransmitter", "Posting Region: $region")
        currentRegion = region
    }

    // Установка текущей страны
    fun postCountry(country: Country?) {
        Log.d("DataTransmitter", "Posting Country: $country")
        currentCountry = country
    }

    // Установка текущей отрасли
    fun postIndustry(industry: Industry?) {
        Log.d("DataTransmitter", "Posting Industry: $industry")
        currentIndustry = industry
    }
}

package ru.practicum.android.diploma.vacancy.filter.data

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import ru.practicum.android.diploma.common.utils.COUNTRY_KEY
import ru.practicum.android.diploma.common.utils.DEFAULT_HIDE_FLAG
import ru.practicum.android.diploma.common.utils.DEFAULT_JSON
import ru.practicum.android.diploma.common.utils.DEFAULT_SALARY
import ru.practicum.android.diploma.common.utils.EXPECTED_SALARY_KEY
import ru.practicum.android.diploma.common.utils.INDUSTRY_KEY
import ru.practicum.android.diploma.common.utils.NOT_SHOW_WITHOUT_SALARY_KEY
import ru.practicum.android.diploma.common.utils.REGION_KEY
import ru.practicum.android.diploma.vacancy.filter.domain.FilterSettingsRepository
import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region

class FilterSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences?,
    private val gson: Gson
) : FilterSettingsRepository {

    private inline fun <reified T> getObject(key: String, defaultValue: T?): T? {
        val json = sharedPreferences?.getString(key, null) ?: return defaultValue
        return gson.fromJson(json, T::class.java)
    }

    private fun saveObject(key: String, value: Any?) {
        val json = gson.toJson(value)
        sharedPreferences?.edit()?.putString(key, json)?.apply()
    }

    override fun saveFilterSettings(filterSettings: FilterSettings) {
        saveObject(COUNTRY_KEY, filterSettings.country)
        saveObject(REGION_KEY, filterSettings.region)
        saveObject(INDUSTRY_KEY, filterSettings.industry)
        sharedPreferences?.edit()
            ?.putInt(EXPECTED_SALARY_KEY, filterSettings.expectedSalary)
            ?.putBoolean(NOT_SHOW_WITHOUT_SALARY_KEY, filterSettings.notShowWithoutSalary)
            ?.apply()
                Log.d("FilterSettings", "Saved Filter Settings: $filterSettings")
    }

    override fun getFilterSettings(): FilterSettings {
      val settings =FilterSettings(
            country = getObject<Country>(COUNTRY_KEY, null),
            region = getObject<Region>(REGION_KEY, null),
            industry = getObject<Industry>(INDUSTRY_KEY, null),
            expectedSalary = sharedPreferences?.getInt(EXPECTED_SALARY_KEY, DEFAULT_SALARY) ?: DEFAULT_SALARY,
            notShowWithoutSalary = sharedPreferences?.
                getBoolean(NOT_SHOW_WITHOUT_SALARY_KEY, DEFAULT_HIDE_FLAG) ?: DEFAULT_HIDE_FLAG
        )
          Log.d("FilterSettings", "Loaded Filter Settings: $settings")
            return settings
    }

    override fun clearFilterSettings() {
        sharedPreferences?.edit()?.apply {
            putString(COUNTRY_KEY, DEFAULT_JSON)
            putString(REGION_KEY, DEFAULT_JSON)
            putString(INDUSTRY_KEY, DEFAULT_JSON)
            putInt(EXPECTED_SALARY_KEY, DEFAULT_SALARY)
            putBoolean(NOT_SHOW_WITHOUT_SALARY_KEY, DEFAULT_HIDE_FLAG)
            apply()
        }
    }
}

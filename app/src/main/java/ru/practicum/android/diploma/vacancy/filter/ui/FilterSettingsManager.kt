package ru.practicum.android.diploma.vacancy.filter.ui

import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings

class FilterSettingsManager(
    private val viewModel: FilterViewModel,
    private val binding: FragmentFilterBinding
) {
    fun updateWorkplaceField(settings: FilterSettings) {
        binding.tiWorkPlace.setText(
            buildString {
                append(settings.country?.name.orEmpty())
                if (!settings.region?.name.isNullOrEmpty()) {
                    if (isNotEmpty()) append(", ")
                    append(settings.region?.name.orEmpty())
                }
            }
        )
    }
}

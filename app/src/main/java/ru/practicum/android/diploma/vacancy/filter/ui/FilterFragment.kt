package ru.practicum.android.diploma.vacancy.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region
import ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry.ChooseIndustryViewModel

class FilterFragment : Fragment() {

    companion object {
        fun newInstance() = FilterFragment()
    }

    private val viewModel: FilterViewModel by viewModel()
    private val industryViewModel: ChooseIndustryViewModel by viewModel()

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val args: FilterFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadFilterSettings()
        observeFilterSettings()
        editingRegioan()
        editingIndustry()
        backToSearch()
        focusPocus()
        setConfirmButtonClickListener()
        resetButtonClickListener()
        observeSelectedIndustry()
        handleWorkplaceData()
        updateHintColorOnTextChange()
        setupClearButtonForSalaryField()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun focusPocus() {
        val textInputLayout = binding.tlSalary
        val editText = binding.tiSalaryField

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayout.defaultHintTextColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
            } else {
                textInputLayout.defaultHintTextColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.search_edit_hint_color))
            }
        }
    }

    private fun backToSearch() {
        binding.toolBarFilter.setNavigationOnClickListener {
            clearFields()
            findNavController().popBackStack(R.id.searchFragment, false)
        }
    }

    private fun editingRegioan() {
        binding.tiWorkPlace.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_chooseWorkplaceFragment)
        }
    }

    private fun editingIndustry() {
        binding.tiIndustryField.setOnClickListener {
            val selectedIndustry = viewModel.selectedIndustry.value
            val action = FilterFragmentDirections
                .actionFilterFragmentToChooseIndustryFragment(selectedIndustry)
            findNavController().navigate(action)

        }
    }

    // Вспомогательная функция для обработки поля зарплаты
    private fun parseExpectedSalary(): Int {
        return binding.tiSalaryField.text.toString().toIntOrNull() ?: -1
    }

    // Создание обновлённого объекта FilterSettings
    private fun createUpdatedFilterSettings(
        expectedSalary: Int,
        notShowWithoutSalary: Boolean,
        oldFilterSettings: FilterSettings?
    ): FilterSettings {
        val country = getValidCountryOrNull(DataTransmitter.getCountry(), oldFilterSettings?.country)
        val region = getValidRegionOrNull(DataTransmitter.getRegion(), oldFilterSettings?.region)
        val industry = getValidIndustryOrNull(DataTransmitter.getIndustry(), oldFilterSettings?.industry)
        return FilterSettings(
            country = country,
            region = region,
            industry = industry,
            expectedSalary = expectedSalary,
            notShowWithoutSalary = notShowWithoutSalary
        )
    }

    private fun getValidCountryOrNull(newCountry: Country?, oldCountry: Country?): Country? {
        return newCountry ?: oldCountry?.takeIf { it.id.isNotEmpty() }
    }

    private fun getValidRegionOrNull(newRegion: Region?, oldRegion: Region?): Region? {
        return newRegion ?: oldRegion?.takeIf { it.id.isNotEmpty() }
    }

    private fun getValidIndustryOrNull(newIndustry: Industry?, oldIndustry: Industry?): Industry? {
        return newIndustry ?: oldIndustry?.takeIf { it.id.isNotEmpty() }
    }

    // Навигация назад
    private fun navigateBackToSearch() {
        findNavController().popBackStack(R.id.searchFragment, false)
    }

    private fun updateButtonsVisibility() {
        val isFilterSet = binding.tiWorkPlace.text?.isNotEmpty() == true ||
            binding.tiIndustryField.text?.isNotEmpty() == true ||
            binding.tiSalaryField.text?.isNotEmpty() == true ||
            binding.checkboxHideWithSalary.isChecked

        showConfirmAndClearButtons(isFilterSet)
    }

    private fun setConfirmButtonClickListener() {
        binding.btnApply.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadFilterSettings()

                val filterSettings = viewModel.filterSettings.value
                val expectedSalary = parseExpectedSalary()
                val notShowWithoutSalary = binding.checkboxHideWithSalary.isChecked

                val updatedFilterSettings =
                    createUpdatedFilterSettings(
                        expectedSalary,
                        notShowWithoutSalary,
                        filterSettings
                    )

                viewModel.saveFilterSettings(updatedFilterSettings)
                clearTemporaryIndustry()
                updateButtonsVisibility()
                navigateBackToSearch()
            }
        }
    }

    private fun resetButtonClickListener() {
        binding.btnReset.setOnClickListener {
            binding.apply {
                tiWorkPlace.setText("")
                tiIndustryField.setText("")
                tiSalaryField.setText("")
                checkboxHideWithSalary.isChecked = false
            }
            viewModel.clearFilterSettings()
            clearTemporaryIndustry()
            showConfirmAndClearButtons(false)
            DataTransmitter.apply {
                postRegion(null)
                postCountry(null)
                postIndustry(null)
                // Реализацию рендера для текстов место работы и отрасль сюда добавьте когда напишете
            }
            updateButtonsVisibility()

        }
    }

    private fun showConfirmAndClearButtons(isVisible: Boolean) {
        binding.btnApply.isVisible = isVisible
        binding.btnReset.isVisible = isVisible
    }

    private fun clearFields() {
        DataTransmitter.postIndustry(null)
        DataTransmitter.postCountry(null)
        DataTransmitter.postRegion(null)
    }

    // тут получаем отрасль из фрагмента выбора отрасли и экран не пересоздается
    private fun observeSelectedIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FilterIndustryValue>("selectedIndustry")
            ?.observe(viewLifecycleOwner) { industry ->
                // Устанавливаем отрасль в текстовое поле
                binding.tlIndustry.editText?.setText(industry.text)
                viewModel.saveIndustry(industry)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    "tempIndustry",
                    industry
                )
                updateButtonsVisibility()
                saveTemporaryIndustry(industry)
            }
    }

    private fun handleWorkplaceData() {
        val args: FilterFragmentArgs by navArgs()
        val countryName = args.countryName
        val cityName = args.cityName
        val workplaceText = buildString {
            if (!countryName.isNullOrEmpty()) append(countryName)
            if (!cityName.isNullOrEmpty()) {
                if (isNotEmpty()) append(", ")
                append(cityName)
            }
        }

        if (workplaceText.isNotEmpty()) {
            binding.tiWorkPlace.setText(workplaceText)
        }
    }

    private fun observeFilterSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterSettings.collect { settings ->
                restoreTemporaryIndustry(settings)

                settings?.let {
                    updateWorkplaceField(it)
                    updateIndustryField(it)
                    updateSalaryField(it)
                    updateCheckbox(it)

                }

            }
        }
    }

    private fun updateWorkplaceField(settings: FilterSettings) {
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

    private fun updateIndustryField(settings: FilterSettings) {
        binding.tiIndustryField.setText(settings.industry?.name.orEmpty())
    }

    private fun updateSalaryField(settings: FilterSettings) {
        binding.tiSalaryField.setText(
            if (settings.expectedSalary >= 0) settings.expectedSalary.toString() else ""
        )
    }

    private fun updateCheckbox(settings: FilterSettings) {
        binding.checkboxHideWithSalary.isChecked = settings.notShowWithoutSalary
    }

    private fun updateHintColorOnTextChange() {
        val layoutWorkPlaceFilter = binding.tlWorkPlaceFilter
        val edittextWorkPlace = binding.tiWorkPlace
        val layoutIndustry = binding.tlIndustry
        val editTextIndustryField = binding.tiIndustryField

        edittextWorkPlace.doOnTextChanged { text, _, _, _ ->
            val hintColor = if (text.isNullOrEmpty()) {
                R.color.search_edit_hint_color
            } else {
                R.color.night_day
            }
            layoutWorkPlaceFilter.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), hintColor))
        }

        editTextIndustryField.doOnTextChanged { text, _, _, _ ->
            val hintColor = if (text.isNullOrEmpty()) {
                R.color.search_edit_hint_color
            } else {
                R.color.night_day
            }
            layoutIndustry.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), hintColor))
        }
        binding.tiSalaryField.doOnTextChanged { text, _, _, _ ->
            updateButtonsVisibility()
        }

        binding.checkboxHideWithSalary.setOnCheckedChangeListener { _, _ ->
            updateButtonsVisibility()
        }
    }

    private fun setupClearButtonForSalaryField() {
        val editTextSalaryField = binding.tiSalaryField
        val clearButton = binding.ivInputButton

        editTextSalaryField.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
        }

        clearButton.setOnClickListener {
            editTextSalaryField.text?.clear()
            clearButton.isVisible = false
        }
    }

    private fun restoreTemporaryIndustry(settings: FilterSettings?) {
        val tempIndustry = findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.get<FilterIndustryValue>("tempIndustry")

        val industryToShow = tempIndustry ?: settings?.industry?.toFilterIndustryValue()
        industryToShow?.let {
            binding.tiIndustryField.setText(it.text) // Используем text вместо name
            viewModel.saveIndustry(it)
        }
    }
    private fun Industry.toFilterIndustryValue(): FilterIndustryValue {
        return FilterIndustryValue(
            id = this.id,
            text = this.name // Устанавливаем text из name
        )
    }
    private fun saveTemporaryIndustry(industry: FilterIndustryValue) {
        findNavController().currentBackStackEntry?.savedStateHandle?.set(
            "tempIndustry",
            industry
        )
    }
    private fun clearTemporaryIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<FilterIndustryValue>("tempIndustry")
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<FilterIndustryValue>("selectedIndustry")
        findNavController().currentBackStackEntry?.savedStateHandle?.set("clearIndustrySelection", true)
        Log.d("FilterFragment", "Industry selection cleared and signal sent")
    }

}

package ru.practicum.android.diploma.vacancy.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.common.utils.DataTransmitter.postIndustry
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterSettings
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region

class FilterFragment : Fragment() {
    companion object {
        private const val TEMP_INDUSTRY_KEY = "tempIndustry"
    }
    private val viewModel: FilterViewModel by viewModel()

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

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
        setupNavigation()
        focusPocus()
        editingIndustry()
        setConfirmButtonClickListener()
        resetButtonClickListener()
        observeSelectedIndustry()
        handleWorkplaceData()
        updateHintColorOnTextChange()
        updateWorkPlaceBasedOnInput()
        updateIndustryBasedOnInput()
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

    private fun setupNavigation() {
        binding.toolBarFilter.setNavigationOnClickListener {
            clearFields()
            findNavController().popBackStack(R.id.searchFragment, false)
        }
        setupNavigationClickListener(binding.tiWorkPlace, R.id.action_filterFragment_to_chooseWorkplaceFragment)
        setupNavigationClickListener(binding.tiIndustryField, R.id.action_filterFragment_to_chooseIndustryFragment)
    }

    private fun setupNavigationClickListener(view: View, navigateAction: Int) {
        view.setOnClickListener {
            findNavController().navigate(navigateAction)
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

    private fun parseExpectedSalary(): Int {
        return binding.tiSalaryField.text.toString().toIntOrNull() ?: -1
    }

    private fun createUpdatedFilterSettings(
        expectedSalary: Int,
        notShowWithoutSalary: Boolean,
        oldFilterSettings: FilterSettings?
    ): FilterSettings {
        val country = getValidCountryOrNull(DataTransmitter.getCountry(), oldFilterSettings?.country)
        val region = getValidRegionOrNull(DataTransmitter.getRegion(), oldFilterSettings?.region)
        val industry = getValidIndustryOrNull(DataTransmitter.getIndustry(), oldFilterSettings?.industry)

        val newSettings = FilterSettings(
            country = country,
            region = region,
            industry = industry,
            expectedSalary = expectedSalary,
            notShowWithoutSalary = notShowWithoutSalary
        )
        return newSettings
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

    private fun navigateBackToSearch() {
        findNavController().popBackStack(R.id.searchFragment, false)
    }

    private fun updateButtonsVisibility() {
        val currentSettings = createUpdatedFilterSettings(
            parseExpectedSalary(),
            binding.checkboxHideWithSalary.isChecked,
            viewModel.filterSettings.value
        )
        DataTransmitter.postCountry(currentSettings.country)
        DataTransmitter.postRegion(currentSettings.region)

        val isFilterChanged = viewModel.hasFilterChanged(currentSettings)
        val hasFilters = hasActiveFilters()

        binding.btnApply.isVisible = isFilterChanged
        binding.btnReset.isVisible = hasFilters
    }

    private fun setConfirmButtonClickListener() {
        binding.btnApply.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val currentSettings = createUpdatedFilterSettings(
                    parseExpectedSalary(),
                    binding.checkboxHideWithSalary.isChecked,
                    viewModel.filterSettings.value
                )

                if (viewModel.hasFilterChanged(currentSettings)) {
                    viewModel.saveFilterSettings(currentSettings)
                    updateButtonsVisibility()
                    navigateBackToSearch()
                }
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
            clearTemporaryIndustry()
            viewModel.clearFilterSettings()
            DataTransmitter.apply {
                postRegion(null)
                postCountry(null)
                postIndustry(null)
            }
            updateButtonsVisibility()
            viewModel.loadFilterSettings()
        }
    }

    private fun clearFields() {
        DataTransmitter.postIndustry(null)
        DataTransmitter.postCountry(null)
        DataTransmitter.postRegion(null)
    }

    private fun observeSelectedIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FilterIndustryValue>("selectedIndustry")
            ?.observe(viewLifecycleOwner) { industry ->
                if (industry == null) {
                    binding.tlIndustry.editText?.setText("")
                    viewModel.saveIndustry(null)
                } else {
                    binding.tlIndustry.editText?.setText(industry.text ?: "")
                    viewModel.saveIndustry(industry)
                }
                updateButtonsVisibility()
                saveTemporaryIndustry(industry)
            }
    }

    private fun handleWorkplaceData() {
        val country = DataTransmitter.getCountry()
        val region = DataTransmitter.getRegion()
        val countryName = country?.name
        val cityName = region?.name
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
                updateButtonsVisibility()
            }
        }
    }

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
    private fun updateWorkPlaceBasedOnInput() {
        binding.apply {
            setupWorkplaceEndIcon(
                textInputLayout = tlWorkPlaceFilter,
                editText = tiWorkPlace,
                navigateAction = R.id.action_filterFragment_to_chooseWorkplaceFragment
            )

        }
    }

    private fun updateIndustryBasedOnInput() {
        binding.apply {
            setupIndustryEndIcon(
                textInputLayout = tlIndustry,
                editText = tiIndustryField,
                navigateAction = R.id.action_filterFragment_to_chooseIndustryFragment
            )
        }
    }

    private fun setupWorkplaceEndIcon(
        textInputLayout: TextInputLayout,
        editText: TextInputEditText,
        navigateAction: Int
    ) {
        editText.doOnTextChanged { text, _, _, _ ->
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(
                textInputLayout.context,
                if (!text.isNullOrEmpty()) R.drawable.ic_clear else R.drawable.ic_arrow_forward
            )
        }
        textInputLayout.setEndIconOnClickListener {
            if (!editText.text.isNullOrEmpty()) {
                viewModel.onClearWorkplacePressed()
                editText.text?.clear()
                updateButtonsVisibility()
            } else {
                findNavController().navigate(navigateAction)
            }
        }
    }

    private fun setupIndustryEndIcon(
        textInputLayout: TextInputLayout,
        editText: TextInputEditText,
        navigateAction: Int
    ) {
        editText.doOnTextChanged { text, _, _, _ ->
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(
                textInputLayout.context,
                if (!text.isNullOrEmpty()) R.drawable.ic_clear else R.drawable.ic_arrow_forward
            )
        }

        textInputLayout.setEndIconOnClickListener {
            if (!editText.text.isNullOrEmpty()) {
                viewModel.onClearIndustryPressed()
                editText.text?.clear()
                clearTemporaryIndustry()
                postIndustry(null)
                updateButtonsVisibility()
            } else {
                findNavController().navigate(navigateAction)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleWorkplaceData()
        updateEndIconsOnResume()
        updateButtonsVisibility()
    }

    private fun restoreTemporaryIndustry(settings: FilterSettings?) {
        val tempIndustry = findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.get<FilterIndustryValue>(TEMP_INDUSTRY_KEY)

        val industryToShow = tempIndustry ?: settings?.industry?.toFilterIndustryValue()
        industryToShow?.let {
            binding.tiIndustryField.setText(it.text)
            viewModel.saveIndustry(it)
        }
    }

    private fun Industry.toFilterIndustryValue(): FilterIndustryValue {
        return FilterIndustryValue(
            id = this.id,
            text = this.name
        )
    }

    private fun saveTemporaryIndustry(industry: FilterIndustryValue?) {
        findNavController().currentBackStackEntry?.savedStateHandle?.set(
            TEMP_INDUSTRY_KEY,
            industry
        )
    }

    private fun clearTemporaryIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<FilterIndustryValue>(TEMP_INDUSTRY_KEY)
        findNavController().currentBackStackEntry?.savedStateHandle?.set("clearIndustrySelection", true)
        findNavController().currentBackStackEntry?.savedStateHandle?.set("selectedIndustry", null)
    }

    private fun updateEndIconsOnResume() {
        val industryText = binding.tiIndustryField.text?.toString()
        val industryIcon = if (!industryText.isNullOrEmpty()) {
            R.drawable.ic_clear
        } else {
            R.drawable.ic_arrow_forward
        }
        binding.tlIndustry.endIconDrawable = ContextCompat.getDrawable(
            requireContext(),
            industryIcon
        )
        val workplaceText = binding.tiWorkPlace.text?.toString()
        val workplaceIcon = if (!workplaceText.isNullOrEmpty()) {
            R.drawable.ic_clear
        } else {
            R.drawable.ic_arrow_forward
        }

        binding.tlWorkPlaceFilter.endIconDrawable = ContextCompat.getDrawable(
            requireContext(),
            workplaceIcon
        )
    }
// test
    private fun hasActiveFilters(): Boolean {
        val workplaceFilled = !binding.tiWorkPlace.text.isNullOrEmpty()
        val industryFilled = !binding.tiIndustryField.text.isNullOrEmpty()
        val salaryFilled = !binding.tiSalaryField.text.isNullOrEmpty()
        val hideWithoutSalaryChecked = binding.checkboxHideWithSalary.isChecked

        return workplaceFilled || industryFilled || salaryFilled || hideWithoutSalaryChecked
    }
}

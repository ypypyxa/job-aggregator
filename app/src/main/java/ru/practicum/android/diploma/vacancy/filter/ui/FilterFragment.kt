package ru.practicum.android.diploma.vacancy.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
//        restoreCheckboxState()
        handleWorkplaceData()
//        applyFilter()
        updateHintColorOnTextChange()
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
        binding.tlWorkPlaceFilter.setEndIconOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_chooseWorkplaceFragment)
            Toast.makeText(requireContext(), "Выбрано место работы", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editingIndustry() {
        binding.tlIndustry.setEndIconOnClickListener {
            // Логика нажатия для поля "Отрасль"
            findNavController().navigate(R.id.action_filterFragment_to_chooseIndustryFragment)
        }
    }

    private fun setConfirmButtonClickListener() {
        binding.btnApply.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadFilterSettings()

                val filterSettings = viewModel.filterSettings.value
                val expectedSalary = parseExpectedSalary()
                val notShowWithoutSalary = binding.checkboxHideWithSalary.isChecked

                val updatedFilterSettings =
                    createUpdatedFilterSettings(expectedSalary, notShowWithoutSalary, filterSettings)

                viewModel.saveFilterSettings(updatedFilterSettings)
                navigateBackToSearch()
            }
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

    private fun resetButtonClickListener() {
        binding.btnReset.setOnClickListener {
            binding.apply {
                tiWorkPlace.setText("")
                tiIndustryField.setText("")
                tiSalaryField.setText("")
                checkboxHideWithSalary.isChecked = false
            }
            viewModel.clearFilterSettings()
            showConfirmAndClearButtons(false)
            DataTransmitter.apply {
                postRegion(null)
                postCountry(null)
                postIndustry(null)
                // Реализацию рендера для текстов место работы и отрасль сюда добавьте когда напишете
            }
        }
    }

    private fun showConfirmAndClearButtons(isVisible: Boolean) {
//        binding.btnApply.isVisible = isVisible
//        binding.btnReset.isVisible = isVisible
    }

    private fun clearFields() {
        DataTransmitter.postIndustry(null)
        DataTransmitter.postCountry(null)
        DataTransmitter.postRegion(null)
    }

    // тут получаем отрасль из фрагмента выбора отрасли и экран не пересоздается
    private fun observeSelectedIndustry() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FilterIndustryValue>(
            "selectedIndustry"
        )?.observe(viewLifecycleOwner) { industry ->
            binding.tlIndustry.editText?.setText(industry.text)
            viewModel.saveIndustry(industry)
            binding.tlIndustry.editText?.setText(industry.text)
        }
    }

    // Кнопка "Применить" ФИЛЬТРАЦИЯ

//    private fun restoreCheckboxState() {
//        val savedStateHandle = findNavController().previousBackStackEntry?.savedStateHandle
//        val savedState = savedStateHandle?.get<Boolean>("onlyWithSalary")
//        binding.checkboxHideWithSalary.isChecked = savedState ?: viewModel.filterSettings.value?.notShowWithoutSalary ?: false
//    }
// private fun applyFilter() {
//    binding.btnApply.setOnClickListener {
//        val onlyWithSalaryChecked = binding.checkboxHideWithSalary.isChecked
//        viewModel.setOnlyWithSalary(onlyWithSalaryChecked) // Сохраняем состояние в ViewModel
//
//        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//            "onlyWithSalary",
//            onlyWithSalaryChecked
//        )
//        findNavController().navigateUp()
//    }
// }

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

        edittextWorkPlace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hintColor = if (s.isNullOrEmpty()) {
                    R.color.search_edit_hint_color
                } else {
                    R.color.night_day
                }
                layoutWorkPlaceFilter.defaultHintTextColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), hintColor))
            }

            override fun afterTextChanged(s: Editable?) {
                // no-op
            }
        })

        editTextIndustryField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hintColor = if (s.isNullOrEmpty()) {
                    R.color.search_edit_hint_color
                } else {
                    R.color.night_day
                }
                layoutIndustry.defaultHintTextColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), hintColor))
            }

            override fun afterTextChanged(s: Editable?) {
                // no-op
            }
        })
    }
}

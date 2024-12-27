package ru.practicum.android.diploma.vacancy.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
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
        editingRegioan()
        editingIndustry()
        backToSearch()
        focusPocus()
        observeSelectedIndustry()
        applyFilter()
        restoreCheckboxState()
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
            findNavController().navigateUp()
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
    private fun applyFilter() {
        binding.btnApply.setOnClickListener {
            val onlyWithSalaryChecked = binding.checkboxHideWithSalary.isChecked
            viewModel.setOnlyWithSalary(onlyWithSalaryChecked) // Сохраняем состояние в ViewModel

            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "onlyWithSalary",
                onlyWithSalaryChecked
            )
            findNavController().navigateUp()
        }
    }

    private fun restoreCheckboxState() {
        val savedStateHandle = findNavController().previousBackStackEntry?.savedStateHandle
        val savedState = savedStateHandle?.get<Boolean>("onlyWithSalary")
        binding.checkboxHideWithSalary.isChecked = savedState ?: false
    }

}

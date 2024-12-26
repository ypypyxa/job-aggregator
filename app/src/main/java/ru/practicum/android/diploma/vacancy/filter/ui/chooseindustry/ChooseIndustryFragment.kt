package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding
import ru.practicum.android.diploma.vacancy.filter.ui.adapter.IndustryAdapter

class ChooseIndustryFragment : Fragment() {

    private val viewModel: ChooseIndustryViewModel by viewModel()
    private var _binding: FragmentChooseIndustryBinding? = null
    private val binding get() = _binding!!
    private var industryAdapter: IndustryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupRecyclerView()
        observeIndustries()
        observeSelectedIndustry()
        backToSearch()
        observeLoadingState()
        observeErrorState()
        setupSearchField()

    }

    private fun backToSearch() {
        binding.chooseIndustryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.chooseButton.setOnClickListener {
            viewModel.selectedIndustry.value?.let { selectedIndustry ->
                val navController = findNavController()

                // Передаем выбранную отрасль
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selectedIndustry",
                    selectedIndustry
                )

                // Очищаем стек до фрагмента фильтра
                navController.popBackStack(R.id.filterFragment, false)
            }
        }
        binding.chooseRegionEnterFieldEdittext.addTextChangedListener { text ->
            viewModel.filterIndustries(text.toString())
        }
    }

    private fun setupRecyclerView() {
        industryAdapter = IndustryAdapter(emptyList()) { industry ->
            // Логика при выборе отрасли
            viewModel.selectIndustry(industry)
        }
        binding.chooseIndustryListRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = industryAdapter
        }
    }

    private fun observeIndustries() {
        lifecycleScope.launchWhenStarted {
            viewModel.industryState.collectLatest { list ->
                industryAdapter = IndustryAdapter(list) { industry ->
                    // Логика нажатия
                    viewModel.selectIndustry(industry)

                }
                binding.chooseIndustryListRecycleView.adapter = industryAdapter
            }
        }
    }

    private fun observeSelectedIndustry() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedIndustry.collectLatest { selectedIndustry ->
                binding.chooseButton.visibility =
                    if (selectedIndustry != null) View.VISIBLE else View.GONE
                industryAdapter?.setSelectedIndustry(selectedIndustry)
            }
        }
    }

    private fun observeLoadingState() {
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBarIndustry.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.chooseIndustryListRecycleView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
    }

    private fun observeErrorState() {
        lifecycleScope.launchWhenStarted {
            viewModel.hasError.collectLatest { hasError ->
                binding.tvErrorMessage.visibility = if (hasError) View.VISIBLE else View.GONE
                binding.chooseIndustryListRecycleView.visibility = if (hasError) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setupSearchField() {
        binding.chooseRegionEnterFieldEdittext.setOnFocusChangeListener { _, hasFocus ->
            toggleSearchIcon(hasFocus, binding.chooseRegionEnterFieldEdittext.text.isNotEmpty())
        }

        binding.clearRegion.setOnClickListener {
            binding.chooseRegionEnterFieldEdittext.text.clear()
            viewModel.filterIndustries("")
            toggleSearchIcon(false, false) // Сбрасываем значок на поиск
        }

        binding.chooseRegionEnterFieldEdittext.addTextChangedListener { text ->
            toggleSearchIcon(true, !text.isNullOrEmpty())
        }
    }
    private fun toggleSearchIcon(hasFocus: Boolean, hasText: Boolean) {
        if (hasFocus && hasText) {
            binding.clearRegion.visibility = View.VISIBLE
            binding.searchRegion.visibility = View.GONE
        } else {
            binding.clearRegion.visibility = View.GONE
            binding.searchRegion.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

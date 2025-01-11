package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.os.Bundle
import android.util.Log
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
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue
import ru.practicum.android.diploma.vacancy.filter.domain.model.Industry
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

    private var preselectedIndustry: FilterIndustryValue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupRecyclerView()
        observeIndustries()
        observeSelectedIndustry()
        backToSearch()
        observeLoadingState()
        observeNoResultsFound()
        observeErrorState()
        setupSearchField()
        observeIndustryReset()
        arguments?.let {
            preselectedIndustry = it.getParcelable("selectedIndustry")
        }
    }

    private fun backToSearch() {
        binding.chooseIndustryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        binding.chooseButton.setOnClickListener {
            viewModel.selectedIndustry.value?.let { selectedIndustry ->
                val industry = selectedIndustry.toIndustry()
                DataTransmitter.postIndustry(industry)
                val navController = findNavController()
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "selectedIndustry",
                    selectedIndustry
                )
                navController.popBackStack(R.id.filterFragment, false)
            }
        }
        binding.chooseRegionEnterFieldEdittext.addTextChangedListener { text ->
            viewModel.filterIndustries(text.toString())
        }
    }

    private fun FilterIndustryValue.toIndustry(): Industry {
        return Industry(
            id = this.id ?: "",
            name = this.text ?: ""
        )
    }

    fun Industry.toFilterIndustryValue(): FilterIndustryValue {
        return FilterIndustryValue(
            id = this.id,
            text = this.name
        )
    }

    private fun setupRecyclerView() {
        industryAdapter = IndustryAdapter(emptyList()) { industry ->
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
                    viewModel.selectIndustry(industry)

                }
                industryAdapter?.setSelectedIndustry(preselectedIndustry)

                binding.chooseIndustryListRecycleView.adapter = industryAdapter
            }
        }
    }

    private fun observeSelectedIndustry() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedIndustry.collectLatest { selectedIndustry ->
                binding.chooseButton.visibility =
                    if (selectedIndustry != null && !viewModel.noResultsFound.value) View.VISIBLE else View.GONE
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
                if (hasError) {
                    binding.tvErrorMessage.text = getString(R.string.search_no_internet)
                    binding.tvErrorMessage.visibility = View.VISIBLE
                    binding.chooseIndustryListRecycleView.visibility = View.GONE
                } else {
                    binding.tvErrorMessage.visibility = View.GONE
                    binding.chooseIndustryListRecycleView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeNoResultsFound() {
        lifecycleScope.launchWhenStarted {
            viewModel.noResultsFound.collectLatest { noResultsFound ->
                if (noResultsFound) {
                    binding.tvErrorMessage.text = getString(R.string.no_industry_found)
                    binding.tvErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.placeholder_nothing_found,
                        0,
                        0
                    )
                    binding.tvErrorMessage.visibility = View.VISIBLE
                    binding.chooseIndustryListRecycleView.visibility = View.GONE
                    binding.chooseButton.visibility = View.GONE
                } else {
                    binding.tvErrorMessage.text = getString(R.string.industry_empty_list)
                    binding.tvErrorMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.placeholder_search_no_internet,
                        0,
                        0
                    )
                    binding.tvErrorMessage.visibility = View.GONE
                    binding.chooseIndustryListRecycleView.visibility = View.VISIBLE
                    binding.chooseButton.visibility = if (viewModel.selectedIndustry.value != null) View.VISIBLE else View.GONE
                }
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
            toggleSearchIcon(false, false)
        }

        binding.chooseRegionEnterFieldEdittext.addTextChangedListener { text ->
            viewModel.filterIndustries(text.toString())
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

    private fun observeIndustryReset() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>("clearIndustrySelection")
            ?.observe(viewLifecycleOwner) { shouldClear ->
                if (shouldClear) {
                    resetIndustrySelection()
                }
            }
    }

    private fun resetIndustrySelection() {
        viewModel.selectIndustry(null)
        industryAdapter?.clearSelection()
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>("clearIndustrySelection")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        observeIndustryReset() // Повторная подписка на сигнал сброса
        Log.d("ChooseIndustryFragment", "onResume triggered observeIndustryReset")
    }
}

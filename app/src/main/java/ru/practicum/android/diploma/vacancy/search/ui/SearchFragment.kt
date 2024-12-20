package ru.practicum.android.diploma.vacancy.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.vacancy.details.ui.DetailsFragment
import ru.practicum.android.diploma.vacancy.search.ui.adapter.VacancyAdapter

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private var onVacancyClickDebounce: ((Int) -> Unit)? = null

    private val vacancyAdapter by lazy {
        VacancyAdapter(emptyList()) { vacancy ->
            onVacancyClickDebounce?.invoke(vacancy.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVacancyClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancyId ->
            findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment,
                DetailsFragment.createArgs(vacancyId)
            )

        }
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onVacancyClickDebounce = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = vacancyAdapter
    }

    private fun setupListeners() {
        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            val isTextEmpty = text.isNullOrEmpty()
            if (isTextEmpty) {
                viewModel.clearVacancies()
                binding.placeholderSearch.isVisible = true
                binding.recyclerView.isVisible = false
                binding.buttonSearch.isVisible = false
                binding.buttonClearEditSearch.isVisible = false
            } else {
                binding.buttonSearch.isVisible = false
                binding.buttonClearEditSearch.isVisible = true
                viewModel.onSearchQueryChanged(text.toString())
            }
        }

        binding.buttonClearEditSearch.setOnClickListener {
            binding.editSearch.text.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.vacancies.observe(viewLifecycleOwner) { vacancies ->
            val hasVacancies = vacancies.isNotEmpty()
            binding.placeholderSearch.isVisible = !hasVacancies && !(viewModel.isLoading.value ?: false)
            binding.recyclerView.isVisible = hasVacancies
            vacancyAdapter.updateVacancies(vacancies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.placeholderSearch.isVisible = !isLoading && binding.editSearch.text.isNullOrEmpty()
        }
    }
}

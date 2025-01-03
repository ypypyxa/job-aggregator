package ru.practicum.android.diploma.vacancy.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.debounce
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import ru.practicum.android.diploma.vacancy.search.ui.adapter.VacancyAdapter
import ru.practicum.android.diploma.vacancy.search.ui.model.SearchFragmentState

class SearchFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L
        const val UNIT = 10
        const val HUNDRED = 100
        val RANGE_EXCEPTION = 12..14
        val RANGE_FEW = 2..4
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
        applyFilterSettingsAndSearch()

        onVacancyClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancyId ->
            val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(vacancyId)
            findNavController().navigate(action)

        }
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        observeFilterResults()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onVacancyClickDebounce = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = vacancyAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!viewModel.isLoading &&
                        visibleItemCount + firstVisibleItemPosition >= totalItemCount - 1
                    ) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }

    private fun setupListeners() {
        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            val isTextEmpty = text.isNullOrEmpty()
            if (isTextEmpty) {
                viewModel.clearVacancies()
                binding.buttonSearch.isVisible = true
                binding.buttonClearEditSearch.isVisible = false
            } else {
                binding.buttonSearch.isVisible = false
                binding.buttonClearEditSearch.isVisible = true
                viewModel.onSearchQueryChanged(text.toString())
            }
        }
        binding.editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val query = binding.editSearch.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.onSearchButtonPress(query)
                }
            }
            false
        }

        binding.buttonClearEditSearch.setOnClickListener {
            binding.editSearch.text.clear()
        }
        binding.buttonFilter.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToFilterFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun render(state: SearchFragmentState) {
        when (state) {
            is SearchFragmentState.Default -> showDefault()
            is SearchFragmentState.Content -> showContent(state.vacancies, state.vacanciesCount)
            is SearchFragmentState.Empty -> showEmpty()
            is SearchFragmentState.ServerError -> showServerError()
            is SearchFragmentState.InternetError -> showInternetError()
            is SearchFragmentState.Loading -> showLoading()
            is SearchFragmentState.UpdateList -> showUpdateList()
        }
    }

    private fun showDefault() {
        hideAll()
        binding.recyclerView.visibility = View.GONE
        binding.placeholderSearch.visibility = View.VISIBLE
    }

    private fun showContent(vacancies: List<VacancySearch>, vacanciesCount: Int) {
        hideAll()
        binding.searchState.setVacancyCountText(vacanciesCount)
        binding.searchState.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        if (viewModel.currentPage <= 0) {
            vacancyAdapter.updateVacancies(vacancies)
        } else {
            vacancyAdapter.addVacancies(vacancies)
        }
    }

    private fun hideAll() {
        binding.placeholderSearch.visibility = View.GONE
        binding.placeholderSearchNoInternet.visibility = View.GONE
        binding.placeholderNothingFound.visibility = View.GONE
        binding.placeholderServerNotResponding.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.progressBarPagination.visibility = View.GONE
        binding.searchState.visibility = View.GONE
    }

    private fun showEmpty() {
        hideAll()
        binding.placeholderNothingFound.visibility = View.VISIBLE
        binding.searchState.text = requireContext().getString(R.string.search_state_nothing_found)
        binding.searchState.visibility = View.VISIBLE
    }

    private fun showServerError() {
        hideAll()
        binding.placeholderServerNotResponding.visibility = View.VISIBLE
    }

    private fun showInternetError() {
        hideAll()
        binding.placeholderSearchNoInternet.visibility = View.VISIBLE
    }

    private fun showLoading() {
        hideAll()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG)
            .show()
    }

    private fun showUpdateList() {
        binding.progressBarPagination.visibility = View.VISIBLE
    }

    private fun TextView.setVacancyCountText(count: Int) {
        val text = when {
            count % UNIT == 1 && count % HUNDRED !in RANGE_EXCEPTION ->
                context.getString(R.string.vacancy_found_one, count)

            count % UNIT in RANGE_FEW && count % HUNDRED !in RANGE_EXCEPTION ->
                context.getString(R.string.vacancy_found_few, count)

            else ->
                context.getString(R.string.vacancy_found_many, count)
        }
        this.text = text
    }

    private fun observeFilterResults() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            "onlyWithSalary"
        )?.observe(viewLifecycleOwner) { onlyWithSalary ->
            viewModel.setOnlyWithSalary(onlyWithSalary)
            viewModel.onSearchQueryChanged(viewModel.latestSearchText ?: "", forceUpdate = true)
        }
    }

    private fun applyFilterSettingsAndSearch() {
        viewModel.getFilterSettings {
            val query = binding.editSearch.text.toString()
            viewModel.onSearchQueryChanged(query, forceUpdate = true)
        }
    }
}

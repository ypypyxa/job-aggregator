package ru.practicum.android.diploma.vacancy.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.vacancy.search.ui.adapter.VacancyAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private val vacancyAdapter by lazy {
        VacancyAdapter(emptyList()) { vacancy ->
            // Обработка нажатия на вакансию при необходимости
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

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val pos = (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemsCount = vacancyAdapter.itemCount
                    if (pos >= itemsCount - 1) {
                        viewModel.onLastItemReached()
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.vacancies.observe(viewLifecycleOwner) { vacancies ->
            hidePlaceholders()
//            val hasVacancies = if (vacancies is VacanciesState.Content) vacancies.vacancies.isEmpty() else false
//            binding.placeholderSearch.isVisible = !hasVacancies && !viewModel.isLoading.value!!
//            binding.recyclerView.isVisible = hasVacancies
//            vacancyAdapter.updateVacancies(vacancies)
            when (vacancies) {
                is VacanciesState.Content -> {
                    vacancyAdapter.updateVacancies(vacancies.vacancies)
                    showVacanciesList()
                }

                is VacanciesState.Error -> {
                    showServerErrorPlaceholder()
                }

                is VacanciesState.Empty -> {
                    showNoResultsPlaceholder()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            hidePlaceholders()
            binding.progressBar.isVisible = isLoading
            binding.placeholderSearch.isVisible = !isLoading && binding.editSearch.text.isNullOrEmpty()
        }
    }

    private fun hidePlaceholders() {
        binding.placeholderSearch.visibility = View.GONE
        binding.placeholderSearchNoInternet.visibility = View.GONE
        binding.placeholderNothingFound.visibility = View.GONE
        binding.placeholderServerNotResponding.visibility = View.GONE
    }

    private fun showVacanciesList() {
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun showNoResultsPlaceholder() {
        binding.recyclerView.visibility = View.GONE
        binding.placeholderNothingFound.visibility = View.VISIBLE
        binding.placeholderSearchNoInternet.visibility = View.GONE
        binding.placeholderServerNotResponding.visibility = View.GONE
    }

    private fun showServerErrorPlaceholder() {
        binding.placeholderServerNotResponding.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.placeholderSearchNoInternet.visibility = View.GONE
        binding.placeholderServerNotResponding.visibility = View.GONE
    }
}

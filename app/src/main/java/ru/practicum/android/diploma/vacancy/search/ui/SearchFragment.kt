package ru.practicum.android.diploma.vacancy.search.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.vacancy.search.ui.adapter.VacancyAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var vacancyAdapter: VacancyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        vacancyAdapter = VacancyAdapter(emptyList()) { vacancy ->
            // Обработка нажатия на вакансию
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = vacancyAdapter
    }

    private fun observeViewModel() {
        viewModel.vacancies.observe(viewLifecycleOwner) { vacancies ->
            Log.d("SearchFragment", "Vacancies observed: ${vacancies.size}")
            vacancyAdapter.updateVacancies(vacancies)
            binding.recyclerView.isVisible = vacancies.isNotEmpty()
            binding.placeholderSearch.isVisible = vacancies.isEmpty()
        }

        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            val isTextEmpty = text.isNullOrEmpty()
            if (isTextEmpty) {
                viewModel.clearVacancies()
                binding.placeholderSearch.isVisible = true
                binding.recyclerView.isVisible = false
            } else {
                binding.buttonSearch.isVisible = false
                binding.buttonClearEditSearch.isVisible = true
                viewModel.onSearchQueryChanged(text.toString())
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.placeholderSearch.isVisible = !isLoading && binding.editSearch.text.isNullOrEmpty()
        }

        binding.buttonClearEditSearch.setOnClickListener {
            binding.editSearch.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

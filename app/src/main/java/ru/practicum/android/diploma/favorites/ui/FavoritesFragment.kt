package ru.practicum.android.diploma.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import ru.practicum.android.diploma.vacancy.search.ui.adapter.VacancyAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private var vacancyAdapter: VacancyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadFavoriteVacancies(INITIAL_PAGE, PAGE_SIZE)
        viewModel.loadFavoriteVacancies(INITIAL_PAGE, PAGE_SIZE)

        if (requireContext().isInternetAvailable()) {
            viewModel.loadFavoriteVacancies(INITIAL_PAGE, PAGE_SIZE)
        } else {
            viewModel.loadFavoriteVacanciesOffline()
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.refreshFavorites() // Обновляем список вакансий при возврате на экран
    }

    private fun setupRecyclerView() {
        vacancyAdapter = VacancyAdapter(emptyList()) { vacancy ->
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment2(vacancy.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vacancyAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoriteVacancies.collect { vacancies ->
                handleState(vacancies)
            }
        }
    }

    private fun handleState(vacancies: List<VacancySearch>) {
        if (_binding == null) return
        binding.progressBar.isVisible = false

        if (vacancies.isEmpty()) {
            binding.llItemList.isVisible = false
            binding.recyclerView.isVisible = false

            if (viewModel.hasLoadedBefore) {
                binding.llFavoriteProblemLayout.isVisible = true
                binding.llFavoriteProblemLayout2.isVisible = false
            } else {
                binding.llFavoriteProblemLayout2.isVisible = true
                binding.llFavoriteProblemLayout.isVisible = false
            }
        } else {
            binding.recyclerView.isVisible = true
            binding.llItemList.isVisible = true
            binding.llFavoriteProblemLayout.isVisible = false
            binding.llFavoriteProblemLayout2.isVisible = false
            vacancyAdapter.updateVacancies(vacancies)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }

}

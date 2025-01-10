package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.common.utils.gone
import ru.practicum.android.diploma.common.utils.show
import ru.practicum.android.diploma.databinding.FragmentChooseRegionBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.adapter.AreaAdapter
import ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model.ChooseRegionFragmentState

class ChooseRegionFragment : Fragment() {

    private var countryId: String? = null

    private val viewModel: ChooseRegionViewModel by viewModel() {
        parametersOf(countryId)
    }

    private var _binding: FragmentChooseRegionBinding? = null
    private val binding get() = _binding!!

    private var areaAdapter: AreaAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ChooseRegionFragmentArgs by navArgs()
        countryId = args.countryId

        setListeners()
        setRecyclerView()
        setObservers()
    }

    private fun setListeners() {
        binding.chooseRegionBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chooseRegionEnterFieldEdittext.doOnTextChanged { text, _, _, _ ->
            val isTextEmpty = text.isNullOrEmpty()
            if (isTextEmpty) {
                binding.icSearchCity.show()
                binding.clearEditSearchCityButton.gone()
                viewModel.onClearSearch()
            } else {
                binding.icSearchCity.gone()
                binding.clearEditSearchCityButton.show()
                viewModel.searchArea(text.toString())
            }
        }
        binding.clearEditSearchCityButton.setOnClickListener {
            binding.chooseRegionEnterFieldEdittext.text.clear()
        }
    }

    private fun setRecyclerView() {
        areaAdapter = AreaAdapter(emptyList()) { area ->
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_area", area)

            findNavController().popBackStack()
        }

        binding.regionListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = areaAdapter
        }
    }

    private fun setObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: ChooseRegionFragmentState) {
        when (state) {
            is ChooseRegionFragmentState.ShowRegions -> showRegions(state.areas)
            is ChooseRegionFragmentState.ShowSearch -> showSearch(state.areas)
            is ChooseRegionFragmentState.NothingFound -> nothingFound()
            is ChooseRegionFragmentState.ShowError -> showError()
            is ChooseRegionFragmentState.Loading -> showLoading()
        }
    }

    private fun showRegions(areas: List<Area>?) {
        areaAdapter?.setAreas(areas!!)
        hidePlaceholders()
        binding.progressBar.gone()
        binding.chooseRegionEnterFieldEdittext.isEnabled = true
        binding.regionListRecyclerView.show()
    }
    private fun showSearch(areas: List<Area>?) {
        areaAdapter?.setAreas(areas!!)
        hidePlaceholders()
        binding.progressBar.gone()
        binding.chooseRegionEnterFieldEdittext.isEnabled = true
        binding.regionListRecyclerView.show()
    }
    private fun nothingFound() {
        binding.regionListRecyclerView.gone()
        binding.progressBar.gone()
        binding.noRegion.show()
    }
    private fun showError() {
        binding.chooseRegionEnterFieldEdittext.isEnabled = false
        binding.regionListRecyclerView.gone()
        binding.progressBar.gone()
        binding.noGetRegionList.show()
    }
    private fun hidePlaceholders() {
        binding.noRegion.gone()
        binding.noGetRegionList.gone()
    }
    private fun showLoading() {
        hidePlaceholders()
        binding.chooseRegionEnterFieldEdittext.isEnabled = false
        binding.regionListRecyclerView.gone()
        binding.progressBar.show()
    }
}

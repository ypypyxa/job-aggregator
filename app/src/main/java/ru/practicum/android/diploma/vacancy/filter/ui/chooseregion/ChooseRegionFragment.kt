package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.databinding.FragmentChooseRegionBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.adapter.AreaAdapter
import ru.practicum.android.diploma.vacancy.filter.ui.chooseregion.model.ChooseRegionFragmentState

class ChooseRegionFragment : Fragment() {

    private var countryId: String? = null
    private var countryName: String? = null

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
    }

    private fun setRecyclerView() {
        areaAdapter = AreaAdapter(emptyList()) { area ->
            when (area.areas) {
                emptyList<Area>() -> {
                    val action = ChooseRegionFragmentDirections
                        .actionChooseRegionFragmentToChooseWorkplaceFragment(
                            countryId = countryId,
                            countryName = countryName,
                            cityId = area.id,
                            cityName = area.name
                        )
                    findNavController().navigate(action)
                }
                else -> viewModel.loadCityByAreaId(area.id)
            }
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
            is ChooseRegionFragmentState.ShowRegion -> showRegion(state.areas, state.countryName)
            is ChooseRegionFragmentState.ShowCity -> showCity(state.areas)
        }
    }

    private fun showRegion(areas: List<Area>, name: String) {
        countryName = name
        areaAdapter?.setAreas(areas)
    }
    private fun showCity(areas: List<Area>) {
        areaAdapter?.setAreas(areas)
    }
}

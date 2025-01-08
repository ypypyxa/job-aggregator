package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.common.utils.gone
import ru.practicum.android.diploma.common.utils.show
import ru.practicum.android.diploma.databinding.FragmentChooseCountryBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.ui.adapter.AreaAdapter
import ru.practicum.android.diploma.vacancy.filter.ui.choosecountry.model.ChooseCountryFragmentState

class ChooseCountryFragment : Fragment() {

    private val viewModel: ChooseCountryViewModel by viewModel()

    private var _binding: FragmentChooseCountryBinding? = null
    private val binding get() = _binding!!

    private var areaAdapter: AreaAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()
        setRecyclerView()
        setObservers()
    }

    fun onBackPressed() {
        binding.chooseCountryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        areaAdapter = AreaAdapter(emptyList()) { area ->
            when (area.id) {
                OTHER_COUNTRIES -> areaAdapter?.setAreas(area.areas)
                else -> {
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_area", Area(area.id, area.name, null, null, emptyList()))

                    findNavController().popBackStack()
                }
            }
        }
        binding.countryListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = areaAdapter
        }
    }

    private fun setObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: ChooseCountryFragmentState) {
        when (state) {
            is ChooseCountryFragmentState.Content -> showContent(state.areas)
            is ChooseCountryFragmentState.Loading -> showLoading()
        }
    }

    private fun showContent(areas: List<Area>) {
        areaAdapter?.setAreas(areas)
        binding.progressBar.gone()
        binding.countryListRecyclerView.show()
    }

    private fun showLoading() {
        binding.countryListRecyclerView.gone()
        binding.progressBar.show()
    }

    companion object {
        private const val OTHER_COUNTRIES = "1001"
    }
}

package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.DataTransmitter
import ru.practicum.android.diploma.common.utils.gone
import ru.practicum.android.diploma.common.utils.show
import ru.practicum.android.diploma.databinding.FragmentChooseWorkplaceBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area
import ru.practicum.android.diploma.vacancy.filter.domain.model.Country
import ru.practicum.android.diploma.vacancy.filter.domain.model.Region
import ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace.model.ChooseWorkplaceFragmentState

class ChooseWorkplaceFragment : Fragment() {

    private var countryId: String? = null
    private var countryName: String? = null
    private var regionId: String? = null
    private var regionName: String? = null

    private val viewModel: ChooseWorkplaceViewModel by viewModel()

    private var _binding: FragmentChooseWorkplaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Area>(SELECTED_AREA)
            ?.observe(viewLifecycleOwner) { regionArea ->
                viewModel.setContent(regionArea)
            }

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.forwardArrowCountry.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
        binding.chooseCountryTextInputEditText.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
        binding.clearCountryButton.setOnClickListener {
            countryId = null
            countryName = null
            regionId = null
            regionName = null
            DataTransmitter.postCountry(null)
            DataTransmitter.postRegion(null)
            val area = Area("", "", null, null, emptyList())
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set(SELECTED_AREA, area)
            viewModel.setContent(area)
        }
        binding.forwardArrowCity.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToChooseRegionFragment(countryId)
            findNavController().navigate(action)
        }
        binding.chooseCityTextInputEditText.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToChooseRegionFragment(countryId)
            findNavController().navigate(action)
        }
        binding.clearCityButton.setOnClickListener {
            regionId = null
            regionName = null
            DataTransmitter.postRegion(null)
            val area = Area(countryId!!, countryName!!, null, null, emptyList())
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.set(SELECTED_AREA, area)
            viewModel.setContent(area)
        }
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.chooseButton.setOnClickListener {
            DataTransmitter.postCountry(Country(id = countryId ?: "", name = countryName ?: ""))
            DataTransmitter.postRegion(Region(id = regionId ?: "", name = regionName ?: ""))
            findNavController().popBackStack(R.id.filterFragment, false)
        }
    }

    private fun setObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: ChooseWorkplaceFragmentState) {
        when (state) {
            is ChooseWorkplaceFragmentState.Empty -> {
                showEmpty()
            }
            is ChooseWorkplaceFragmentState.CountrySelected -> {
                setCountryName(state.area)
            }
            is ChooseWorkplaceFragmentState.RegionSelected -> {
                setCityName(state.area)
            }
        }
    }

    private fun showEmpty() {
        binding.chooseCountryTextInputEditText.text?.clear()
        binding.chooseCityTextInputEditText.text?.clear()
        binding.chooseCountryTextInputEditText.isEnabled = true
        binding.chooseCityTextInputEditText.isEnabled = true
        binding.clearCountryButton.gone()
        binding.forwardArrowCountry.show()
        binding.clearCityButton.gone()
        binding.forwardArrowCity.show()
    }

    private fun setCountryName(area: Area) {
        countryId = area.id
        countryName = area.name
        binding.chooseCountryTextInputEditText.setText(countryName)
        binding.chooseCityTextInputEditText.text?.clear()
        binding.chooseCountryTextInputEditText.isEnabled = false
        binding.chooseCityTextInputEditText.isEnabled = true
        binding.clearCountryButton.show()
        binding.forwardArrowCountry.gone()
        binding.clearCityButton.gone()
        binding.forwardArrowCity.show()
    }

    private fun setCityName(area: Area) {
        countryId = area.parentId
        countryName = area.parentName
        regionId = area.id
        regionName = area.name
        binding.chooseCountryTextInputEditText.setText(countryName)
        binding.chooseCityTextInputEditText.setText(regionName)
        binding.chooseCountryTextInputEditText.isEnabled = false
        binding.chooseCityTextInputEditText.isEnabled = false
        binding.clearCountryButton.show()
        binding.forwardArrowCountry.gone()
        binding.clearCityButton.show()
        binding.forwardArrowCity.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SELECTED_AREA = "selected_area"
    }
}

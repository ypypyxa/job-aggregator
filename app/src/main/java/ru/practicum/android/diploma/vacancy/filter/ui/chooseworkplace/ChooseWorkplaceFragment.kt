package ru.practicum.android.diploma.vacancy.filter.ui.chooseworkplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
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
    private var cityId: String? = null
    private var cityName: String? = null

    private val viewModel: ChooseWorkplaceViewModel by viewModel() {
        parametersOf(countryName, cityName)
    }

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

        val args: ChooseWorkplaceFragmentArgs by navArgs()
        countryId = args.countryId
        countryName = args.countryName
        cityId = args.cityId
        cityName = args.cityName

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Area>("selected_country")
            ?.observe(viewLifecycleOwner) { countryArea ->
                countryId = countryArea.id
                countryName = countryArea.name

                viewModel.setCountry(countryName)
            }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Area>("selected_region")
            ?.observe(viewLifecycleOwner) { regionArea ->
                cityId = regionArea.id
                cityName = regionArea.name
                if (!regionArea.parentName.isNullOrEmpty()) {
                    countryName = regionArea.parentName
                    countryId = regionArea.parentId
                }
                viewModel.setCity(countryName, cityName)
            }

        setListeners()
        setObservers()

        binding.clearCountryButton.setOnClickListener {
            clearCountryAndCity()
        }
        binding.clearCityButton.setOnClickListener {
            clearCountryAndCity()
        }

    }

    private fun setListeners() {
        binding.forwardArrowCountry.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
        binding.clearCountryButton.setOnClickListener {
            countryId = null
            countryName = null
            cityId = null
            cityName = null
            DataTransmitter.postCountry(null)
            DataTransmitter.postRegion(null)
        }
        binding.forwardArrowCity.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToChooseRegionFragment(countryId)
            findNavController().navigate(action)
        }
        binding.clearCityButton.setOnClickListener {
            setCountryName(countryName)
            cityId = null
            cityName = null
            DataTransmitter.postRegion(null)
        }
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.chooseButton.setOnClickListener {
            DataTransmitter.postCountry(Country(id = countryId ?: "", name = countryName ?: ""))
            DataTransmitter.postRegion(Region(id = cityId ?: "", name = cityName ?: ""))
            findNavController().popBackStack(R.id.filterFragment, false)
        }
    }

    private fun setObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: ChooseWorkplaceFragmentState) {
        if (
            state is ChooseWorkplaceFragmentState.Empty &&
            !cityName.isNullOrEmpty()
        ) {
            setCityName(countryName, cityName)
            return
        }
        when (state) {
            is ChooseWorkplaceFragmentState.Empty -> {
                showEmpty()
            }
            is ChooseWorkplaceFragmentState.CountrySelected -> {
                setCountryName(state.name)
            }
            is ChooseWorkplaceFragmentState.CitySelected -> {
                setCityName(state.countryName, state.cityName)
            }
        }
    }

    private fun showEmpty() {
        binding.chooseCountryTextInputEditText.text?.clear()
        binding.chooseCityTextInputEditText.text?.clear()

        binding.clearCountryButton.gone()
        binding.forwardArrowCountry.show()
        binding.clearCityButton.gone()
        binding.forwardArrowCity.show()
    }

    private fun setCountryName(name: String?) {
        binding.chooseCountryTextInputEditText.setText(name)
        binding.chooseCityTextInputEditText.text?.clear()
        binding.forwardArrowCity.isEnabled = true
        binding.chooseCountryTextInputLayout.isEnabled = true
        binding.clearCountryButton.show()
        binding.forwardArrowCountry.gone()
        binding.clearCityButton.gone()
        binding.forwardArrowCity.show()
    }

    private fun setCityName(countryName: String?, cityName: String?) {
        binding.chooseCountryTextInputEditText.setText(countryName)
        binding.chooseCityTextInputEditText.setText(cityName)
        binding.clearCountryButton.show()
        binding.forwardArrowCountry.gone()
        binding.clearCityButton.show()
        binding.forwardArrowCity.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearCountryAndCity() {
        countryId = null
        countryName = null
        cityId = null
        cityName = null

        viewModel.setCountry(null)

        DataTransmitter.postCountry(null)
        DataTransmitter.postRegion(null)
    }
}

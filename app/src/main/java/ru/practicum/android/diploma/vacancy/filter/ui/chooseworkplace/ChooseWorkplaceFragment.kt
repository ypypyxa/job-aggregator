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
import ru.practicum.android.diploma.databinding.FragmentChooseWorkplaceBinding
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

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.forwardArrowCountry.setOnClickListener() {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
        binding.chooseCountryTextInputEditText.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
        binding.forwardArrowRegion.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToChooseRegionFragment(countryId)
            findNavController().navigate(action)
        }
        binding.chooseCityTextInputEditText.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToChooseRegionFragment(countryId)
            findNavController().navigate(action)
        }
        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_filterFragment)
        }
        binding.chooseButton.setOnClickListener {
            val action = ChooseWorkplaceFragmentDirections
                .actionChooseWorkplaceFragmentToFilterFragment(
                    countryId = countryId,
                    countryName = countryName,
                    cityId = cityId,
                    cityName = cityName)
            findNavController().navigate(action)
        }
    }

    private fun setObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: ChooseWorkplaceFragmentState) {
        when (state) {
            is ChooseWorkplaceFragmentState.Empty -> showEmpty()
            is ChooseWorkplaceFragmentState.CountrySelected -> setCountryName(state.name)
            is ChooseWorkplaceFragmentState.CitySelected -> setCityName(state.countryName, state.cityName)
        }
    }

    private fun showEmpty() {
        binding.chooseCountryTextInputEditText.text?.clear()
        binding.chooseCityTextInputEditText.text?.clear()
        binding.forwardArrowRegion.isEnabled = false
        binding.chooseCountryTextInputLayout.isEnabled = false
    }

    private fun setCountryName(name: String?) {
        binding.chooseCountryTextInputEditText.setText(name)
        binding.forwardArrowRegion.isEnabled = true
        binding.chooseCountryTextInputLayout.isEnabled = true
    }

    private fun setCityName(countryName: String?, cityName: String?) {
        binding.chooseCountryTextInputEditText.setText(countryName)
        binding.chooseCityTextInputEditText.setText(cityName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

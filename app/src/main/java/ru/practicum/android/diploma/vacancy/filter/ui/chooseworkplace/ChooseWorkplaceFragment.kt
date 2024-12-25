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

    private val viewModel: ChooseWorkplaceViewModel by viewModel() {
        parametersOf(countryName)
    }

    private var countryName: String = ""

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
        countryName = args.countryName

        setListeners()
        setObservers()
        selectCountry()
        selectRegion()
        onBackPressed()
    }

    private fun setListeners() {
        binding.forwardArrowCountry.setOnClickListener() {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
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
            is ChooseWorkplaceFragmentState.CitySelected -> setCityName(state.name)
        }
    }

    private fun showEmpty() {
        binding.chooseCountryTextInputEditText.text?.clear()
        binding.chooseCityTextInputEditText.text?.clear()
    }

    private fun setCountryName(name: String) {
        binding.chooseCountryTextInputEditText.setText(name)
    }

    private fun setCityName(name: String) {
        binding.chooseCityTextInputEditText.setText(name)
    }

    fun selectCountry() {
        binding.chooseCountryTextInputEditText.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseCountryFragment)
        }
    }

    fun selectRegion() {
        binding.chooseCityTextInputEditText.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_chooseRegionFragment)
        }
    }

    fun onBackPressed() {
        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkplaceFragment_to_filterFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

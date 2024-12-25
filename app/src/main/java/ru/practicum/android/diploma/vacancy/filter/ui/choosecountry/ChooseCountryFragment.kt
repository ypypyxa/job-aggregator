package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentChooseCountryBinding

class ChooseCountryFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseCountryFragment()
    }

    private val viewModel: ChooseCountryViewModel by viewModel()

    private var _binding: FragmentChooseCountryBinding? = null
    private val binding get() = _binding!!

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

        viewModel.loadVacancy()
        viewModel.loadVacancyById("1620")
        onBackPressed()
    }

    fun onBackPressed() {
        binding.chooseCountryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding

class ChooseIndustryFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseIndustryFragment()
    }

    private val viewModel: ChooseIndustryViewModel by viewModels()

    private var _binding: FragmentChooseIndustryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
    }

    fun onBackPressed() {
        binding.chooseIndustryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}

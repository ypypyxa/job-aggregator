package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.databinding.FragmentChooseRegionBinding

class ChooseRegionFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseRegionFragment()
    }

    private val viewModel: ChooseRegionViewModel by viewModels()

    private var _binding: FragmentChooseRegionBinding? = null
    private val binding get() = _binding!!

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
        onBackPressed()
    }

    fun onBackPressed() {
        binding.chooseRegionBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

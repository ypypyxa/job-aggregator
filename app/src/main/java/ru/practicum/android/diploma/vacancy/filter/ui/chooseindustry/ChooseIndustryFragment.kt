package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentChooseIndustryBinding
import ru.practicum.android.diploma.vacancy.filter.ui.adapter.IndustryAdapter

class ChooseIndustryFragment : Fragment() {

    private val viewModel: ChooseIndustryViewModel by viewModel()
    private var _binding: FragmentChooseIndustryBinding? = null
    private val binding get() = _binding!!
    private var industryAdapter: IndustryAdapter? = null

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
        setupRecyclerView()
        observeIndustries()
    }

    fun onBackPressed() {
        binding.chooseIndustryBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        industryAdapter = IndustryAdapter(emptyList()) { industry ->
            // Логика при выборе отрасли
        }
        binding.chooseIndustryListRecycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = industryAdapter
        }
    }

    private fun observeIndustries() {
        lifecycleScope.launchWhenStarted {
            viewModel.industryState.collectLatest { list ->
                industryAdapter = IndustryAdapter(list) { industry ->
                    // Логика нажатия
                }
                binding.chooseIndustryListRecycleView.adapter = industryAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

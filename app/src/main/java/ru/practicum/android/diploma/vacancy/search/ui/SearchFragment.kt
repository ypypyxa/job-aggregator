package ru.practicum.android.diploma.vacancy.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadVacancies()

        binding.editSearch.doOnTextChanged { text, _, _, _ ->
            val isTextEmpty = text.isNullOrEmpty()
            binding.buttonSearch.isVisible = isTextEmpty
            binding.buttonClearEditSearch.isVisible = !isTextEmpty
            viewModel.onSearchQueryChanged(text.toString())
            if (isTextEmpty) {
                binding.placeholderSearch.isVisible = true
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.placeholderSearch.isVisible = !isLoading && binding.editSearch.text.isNullOrEmpty()
        }

        binding.buttonClearEditSearch.setOnClickListener {
            binding.editSearch.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

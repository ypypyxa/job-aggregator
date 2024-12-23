package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.practicum.android.diploma.R

class ChooseIndustryFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseIndustryFragment()
    }

    private val viewModel: ChooseIndustryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_choose_industry, container, false)
    }
}

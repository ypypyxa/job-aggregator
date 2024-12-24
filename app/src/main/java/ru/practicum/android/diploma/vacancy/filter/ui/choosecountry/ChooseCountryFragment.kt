package ru.practicum.android.diploma.vacancy.filter.ui.choosecountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R

class ChooseCountryFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseCountryFragment()
    }

    private val viewModel: ChooseCountryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_choose_country, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadVacancy()
        viewModel.loadVacancyById("1620")
    }
}

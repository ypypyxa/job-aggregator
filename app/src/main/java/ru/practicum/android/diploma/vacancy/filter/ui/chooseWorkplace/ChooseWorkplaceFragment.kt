package ru.practicum.android.diploma.vacancy.filter.ui.chooseWorkplace

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.practicum.android.diploma.R

class ChooseWorkplaceFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseWorkplaceFragment()
    }

    private val viewModel: ChooseWorkplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_choose_workplace, container, false)
    }
}

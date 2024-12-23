package ru.practicum.android.diploma.vacancy.filter.ui.chooseregion

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.practicum.android.diploma.R

class ChooseRegionFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseRegionFragment()
    }

    private val viewModel: ChooseRegionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_choose_region, container, false)
    }
}

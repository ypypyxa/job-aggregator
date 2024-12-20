package ru.practicum.android.diploma.vacancy.details.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R

class DetailsFragment : Fragment() {

    companion object {
        private const val ARGS_VACANCY_ID = "vacancy_id"
        private const val NULL_ID = 0

        fun createArgs(vacancyId: Int): Bundle =
            bundleOf(ARGS_VACANCY_ID to vacancyId)
    }

    private val viewModel: DetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacancyId = requireArguments().getInt(ARGS_VACANCY_ID) ?: NULL_ID

        Log.d("VacancyID", "$vacancyId")
        viewModel.loadVacancy(vacancyId)
    }
}

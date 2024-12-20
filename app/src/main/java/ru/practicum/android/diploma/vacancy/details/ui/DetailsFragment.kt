package ru.practicum.android.diploma.vacancy.details.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    companion object {
        private const val ARGS_VACANCY_ID = "vacancy_id"
        private const val NULL_ID = 0

        fun createArgs(vacancyId: Int): Bundle = bundleOf(ARGS_VACANCY_ID to vacancyId)
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacancyId = requireArguments().getInt(ARGS_VACANCY_ID) ?: NULL_ID

        binding.tvStateError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        Log.d("VacancyID", "$vacancyId")
        viewModel.loadVacancy(vacancyId)

        viewModel.vacancyDetails.observe(viewLifecycleOwner) { vacancyDetails ->
            vacancyDetails?.let {
                // Заголовок
                binding.tvVacancyName.text = it.title
                // ЗП
                binding.tvSalary.text = formatSalary(it.salaryFrom, it.salaryTo, it.currency)
                // Название компании
                binding.tvEmployerText.text = it.employerName
                // город
                binding.tvCityText.text = it.city
                // опыт
                binding.tvExperience.text = it.experience
                // ЛОГО
                Glide.with(binding.ivEmployerLogo.context)
                    .load(it.employerLogoUri)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.ivEmployerLogo)
                // График работы
                binding.tvSchedule.text = it.schedule
                // Веб текст вывод
                binding.wvDescription.loadData(
                    it.jobDescription ?: "",
                    "text/html; charset=UTF-8",
                    "UTF-8"
                )

            }
        }
        // Назад
        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun formatSalary(salaryFrom: Int?, salaryTo: Int?, currency: String?): String {
        return when {
            salaryFrom != null && salaryTo != null -> {
                "ЗП от $salaryFrom до $salaryTo ${currency ?: ""}"
            }
            salaryFrom != null -> {
                "ЗП от $salaryFrom ${currency ?: ""}"
            }
            salaryTo != null -> {
                "ЗП до $salaryTo ${currency ?: ""}"
            }
            else -> {
                "Зарплата не указана"
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package ru.practicum.android.diploma.vacancy.details.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails

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
        Log.d("VacancyID", "$vacancyId")

        setupViews()
        observeViewModel()
        viewModel.loadVacancy(vacancyId)

    }

    private fun setupViews() {
        binding.tvStateError.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivLikeButton.setOnClickListener {
            val vacancy = viewModel.vacancyDetails.value
            vacancy?.let {
                if (viewModel.isFavorite.value == true) {
                    viewModel.removeFromFavorites(it.vacancyId)
                } else {
                    viewModel.addToFavorites(it)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.vacancyDetails.observe(viewLifecycleOwner) { vacancyDetails ->
            updateUI(vacancyDetails)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isFavorite.collect { isFavorite ->
                val iconRes = if (isFavorite == true) R.drawable.ic_heart_active else R.drawable.ic_heart
                binding.ivLikeButton.setImageResource(iconRes)
            }
        }

    }

    private fun updateUI(vacancyDetails: VacancyDetails?) {
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
            binding.wvDescription.settings.javaScriptEnabled = true
            val isNightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
            val textColor = if (isNightMode) "#FDFDFD" else "#1A1B22"
            val backgroundColor = if (isNightMode) "#1A1B22" else "#FDFDFD"
            val jobDescriptionHtml = """
    <html>
    <head>
        <style>
            body {
                color: $textColor;
                background-color: $backgroundColor;
                margin: 0;
                padding: 0;
            }
        </style>
    </head>
    <body>
        ${vacancyDetails.jobDescription ?: ""}
    </body>
    </html>
            """.trimIndent()
            binding.wvDescription.loadDataWithBaseURL(null, jobDescriptionHtml, "text/html", "UTF-8", null)

        }
    }

    private fun formatSalary(salaryFrom: Int?, salaryTo: Int?, currency: String?): String {
        return when {
            salaryFrom != null && salaryTo != null -> "ЗП от $salaryFrom до $salaryTo ${currency ?: ""}"
            salaryFrom != null -> "ЗП от $salaryFrom ${currency ?: ""}"
            salaryTo != null -> "ЗП до $salaryTo ${currency ?: ""}"
            else -> "Зарплата не указана"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

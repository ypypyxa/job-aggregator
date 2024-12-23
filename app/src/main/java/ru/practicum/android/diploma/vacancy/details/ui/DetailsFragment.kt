package ru.practicum.android.diploma.vacancy.details.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.utils.gone
import ru.practicum.android.diploma.common.utils.show
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.vacancy.details.domain.model.VacancyDetails
import ru.practicum.android.diploma.vacancy.details.ui.model.DetailsFragmentState

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val corner by lazy { requireContext().resources.getDimension(R.dimen.dimens_12).toInt() }

    private var vacancy: VacancyDetails? = null
    private var vacancyId: Int? = 0
    private var vacancyUrl: String? = ""

    private val viewModel: DetailsViewModel by viewModel() {
        parametersOf(vacancyId)
    }

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

        val args: DetailsFragmentArgs by navArgs()
        vacancyId = args.vacancyId

        hideAll()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivLikeButton.setOnClickListener {
            vacancy?.let {
                if (viewModel.isFavorite.value == true) {
                    viewModel.removeFromFavorites(it.vacancyId)
                } else {
                    viewModel.addToFavorites(it)
                }
            }
        }

        binding.ivShareButton.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, vacancyUrl)
                type = "text/plain"
            }
            val share = Intent.createChooser(intent, null)
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(share)
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is DetailsFragmentState.Loading -> showLoading()
                is DetailsFragmentState.Empty -> showEmpty()
                is DetailsFragmentState.ServerError -> showServerError()
                is DetailsFragmentState.Content -> updateUI(it.vacancy)
                is DetailsFragmentState.OfflineContent -> updateUI(it.vacancy)
            }
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
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
            setupBasicInfo(it)
            setupDescription(it)
        }
    }

    private fun setupBasicInfo(vacancyDetails: VacancyDetails) {
        vacancyDetails?.let {
            binding.tvVacancyName.text = it.title
            binding.tvSalary.text = formatSalary(it.salaryFrom, it.salaryTo, it.currency)
            binding.tvEmployerText.text = it.employerName
            binding.tvCityText.text = it.city
            binding.tvExperience.text = it.experience
            Glide
                .with(binding.ivEmployerLogo.context)
                .load(it.employerLogoUri)
                .fitCenter()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(corner))
                .into(binding.ivEmployerLogo)
            binding.tvSchedule.text = it.schedule
        }
    }

    private fun setupDescription(vacancyDetails: VacancyDetails) {
        binding.wvDescription.settings.javaScriptEnabled = true
        val isNightMode =
            resources.configuration.uiMode and
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
        hideAll()
        binding.clBody.show()
        vacancy = vacancyDetails
    }

    private fun showLoading() {
        hideAll()
        binding.progressBar.show()
    }

    private fun showEmpty() {
        hideAll()
        binding.tvVacancyError.show()
    }

    private fun showServerError() {
        hideAll()
        binding.tvStateError.show()
    }

    private fun hideAll() {
        binding.progressBar.gone()
        binding.tvStateError.gone()
        binding.tvVacancyError.gone()
        binding.clBody.gone()
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

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }
}

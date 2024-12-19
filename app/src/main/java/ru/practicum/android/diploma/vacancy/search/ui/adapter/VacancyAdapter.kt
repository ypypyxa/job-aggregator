package ru.practicum.android.diploma.vacancy.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.dto.CurrencyIds
import ru.practicum.android.diploma.databinding.ItemVacancyBinding
import ru.practicum.android.diploma.vacancy.search.domain.model.SalaryData
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import java.text.NumberFormat
import java.util.Locale

class VacancyAdapter(
    private var vacancies: List<VacancySearch>,
    private val onVacancyClick: (VacancySearch) -> Unit
) : RecyclerView.Adapter<VacancyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVacancyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onVacancyClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(vacancies[position])
    }

    override fun getItemCount(): Int = vacancies.size

    class ViewHolder(
        private val binding: ItemVacancyBinding,
        private val onVacancyClick: (VacancySearch) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vacancy: VacancySearch) {
            Glide.with(binding.itemVacancyCompanyImage.context)
                .load(vacancy.logo)
                .placeholder(R.drawable.placeholder)
                .into(binding.itemVacancyCompanyImage)

            binding.itemVacancyName.text = vacancy.name
            binding.itemVacancyCompanyName.text = vacancy.company
                ?: itemView.context.getString(R.string.company_not_specified)

            val salaryString = vacancy.salary
            val salaryData = if (!salaryString.isNullOrBlank()) parseSalaryData(salaryString) else null
            binding.itemVacancySalary.text = salaryData?.let { formatSalary(it) }
                ?: itemView.context.getString(R.string.message_salary_not_pointed)

            itemView.setOnClickListener { onVacancyClick(vacancy) }
        }

        private fun parseSalaryData(salaryString: String): SalaryData? {
            if (!salaryString.startsWith("SalaryDto")) return null

            val from = Regex("from=(\\d+)").find(salaryString)?.groupValues?.get(1)?.toIntOrNull()
            val to = Regex("to=(\\d+)").find(salaryString)?.groupValues?.get(1)?.toIntOrNull()
            val currencyCode = Regex("currency=([A-Z]+)").find(salaryString)?.groupValues?.get(1)
            val currencySymbol = currencyCode?.let { CurrencyIds.getSymbol(it) } ?: ""

            return if (from != null || to != null) {
                SalaryData(from, to, currencySymbol)
            } else {
                null
            }
        }

        private fun formatSalary(data: SalaryData): String {
            return when {
                data.from != null && data.to != null -> {
                    itemView.context.getString(
                        R.string.salary_template_from_to,
                        formatNumber(data.from),
                        formatNumber(data.to),
                        data.currencySymbol
                    )
                }

                data.from == null && data.to != null -> {
                    itemView.context.getString(
                        R.string.salary_template_to,
                        formatNumber(data.to),
                        data.currencySymbol
                    )
                }

                data.from != null && data.to == null -> {
                    itemView.context.getString(
                        R.string.salary_template_from,
                        formatNumber(data.from),
                        data.currencySymbol
                    )
                }

                else -> itemView.context.getString(R.string.message_salary_not_pointed)
            }
        }

        fun formatNumber(value: Int?): String {
            if (value == null) return ""
            val formatter = NumberFormat.getInstance(Locale("ru"))
            return formatter.format(value)
        }
    }

    fun updateVacancies(newVacancies: List<VacancySearch>) {
        vacancies = newVacancies
        notifyDataSetChanged()
    }
}

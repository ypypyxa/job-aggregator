package ru.practicum.android.diploma.vacancy.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.dto.CurrencyIds
import ru.practicum.android.diploma.vacancy.search.domain.model.VacancySearch
import java.text.NumberFormat
import java.util.Locale

class VacancyAdapter(
    private var vacancies: List<VacancySearch>,
    private val onVacancyClick: (VacancySearch) -> Unit
) : RecyclerView.Adapter<VacancyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vacancy, parent, false)
        return ViewHolder(view, onVacancyClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(vacancies[position])
    }

    override fun getItemCount(): Int = vacancies.size

    class ViewHolder(
        itemView: View,
        private val onVacancyClick: (VacancySearch) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val companyImage: ImageView = itemView.findViewById(R.id.item_vacancy_company_image)
        private val vacancyName: TextView = itemView.findViewById(R.id.item_vacancy_name)
        private val companyName: TextView = itemView.findViewById(R.id.item_vacancy_company_name)
        private val salary: TextView = itemView.findViewById(R.id.item_vacancy_salary)

        fun bind(vacancy: VacancySearch) {
            Glide.with(companyImage.context)
                .load(vacancy.logo)
                .placeholder(R.drawable.placeholder)
                .into(companyImage)

            vacancyName.text = vacancy.name
            companyName.text = vacancy.company ?: itemView.context.getString(R.string.company_not_specified)

            val salaryString = vacancy.salary
            if (!salaryString.isNullOrBlank() && salaryString.startsWith("SalaryDto")) {
                try {
                    val from = Regex("from=(\\d+)").find(salaryString)?.groupValues?.get(1)?.toIntOrNull()
                    val to = Regex("to=(\\d+)").find(salaryString)?.groupValues?.get(1)?.toIntOrNull()
                    val currencyCode = Regex("currency=([A-Z]+)").find(salaryString)?.groupValues?.get(1)
                    val currencySymbol = currencyCode?.let { CurrencyIds.getSymbol(it) } ?: ""

                    salary.text = when {
                        from != null && to != null ->
                            itemView.context.getString(
                                R.string.salary_template_from_to,
                                formatNumber(from),
                                formatNumber(to),
                                currencySymbol
                            )
                        from == null && to != null ->
                            itemView.context.getString(
                                R.string.salary_template_to,
                                formatNumber(to),
                                currencySymbol
                            )
                        from != null && to == null ->
                            itemView.context.getString(
                                R.string.salary_template_from,
                                formatNumber(from),
                                currencySymbol
                            )
                        else -> itemView.context.getString(R.string.message_salary_not_pointed)
                    }
                } catch (e: Exception) {
                    salary.text = itemView.context.getString(R.string.message_salary_not_pointed)
                }
            } else {
                salary.text = itemView.context.getString(R.string.message_salary_not_pointed)
            }

            itemView.setOnClickListener { onVacancyClick(vacancy) }
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


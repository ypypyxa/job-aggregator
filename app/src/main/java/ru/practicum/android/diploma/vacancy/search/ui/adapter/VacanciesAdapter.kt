package ru.practicum.android.diploma.vacancy.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.network.response.Vacancy

class VacancyAdapter(
    private var vacancies: List<Vacancy>,
    private val onVacancyClick: (Vacancy) -> Unit
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
        private val onVacancyClick: (Vacancy) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val companyImage: ImageView = itemView.findViewById(R.id.item_vacancy_company_image)
        private val vacancyName: TextView = itemView.findViewById(R.id.item_vacancy_name)
        private val companyName: TextView = itemView.findViewById(R.id.item_vacancy_company_name)
        private val salary: TextView = itemView.findViewById(R.id.item_vacancy_salary)

        fun bind(vacancy: Vacancy) {
            Glide.with(companyImage.context)
                .load(vacancy.employer.name)
                .placeholder(R.drawable.placeholder)
                .into(companyImage)

            vacancyName.text = vacancy.name
            companyName.text = vacancy.employer.name

            salary.text = vacancy.salary?.let {
                val from = it.from?.toString() ?: "?"
                val to = it.to?.toString() ?: "?"
                "$from - $to ${it.currency}"
            } ?: "Зарплата не указана"

            itemView.setOnClickListener { onVacancyClick(vacancy) }
        }
    }

    fun updateVacancies(newVacancies: List<Vacancy>) {
        vacancies = newVacancies
        notifyDataSetChanged()
    }
}


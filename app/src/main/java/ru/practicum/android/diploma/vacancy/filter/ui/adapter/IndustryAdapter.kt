package ru.practicum.android.diploma.vacancy.filter.ui.chooseindustry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class IndustryAdapter(
    private val industries: List<FilterIndustryValue>,
    private val onClick: (FilterIndustryValue) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    inner class IndustryViewHolder(
        private val binding: ItemIndustryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(industry: FilterIndustryValue) {
            binding.industryRegionTitleTextview.text = industry.text
            itemView.setOnClickListener {
                onClick(industry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val binding = ItemIndustryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IndustryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        holder.bind(industries[position])
    }

    override fun getItemCount(): Int = industries.size
}

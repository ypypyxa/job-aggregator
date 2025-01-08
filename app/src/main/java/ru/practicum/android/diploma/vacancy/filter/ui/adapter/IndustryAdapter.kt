package ru.practicum.android.diploma.vacancy.filter.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemIndustryBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.FilterIndustryValue

class IndustryAdapter(
    private var industries: List<FilterIndustryValue>,
    private val onClick: (FilterIndustryValue) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var selectedIndustry: FilterIndustryValue? = null

    inner class IndustryViewHolder(
        private val binding: ItemIndustryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(industry: FilterIndustryValue) {
            binding.industryRegionTitleTextview.text = industry.text
            binding.industryRegionCheckRadiobutton.isChecked = industry == selectedIndustry

            binding.root.setOnClickListener {
                onClick(industry)
                selectedIndustry = industry
                notifyDataSetChanged()
            }

            binding.industryRegionCheckRadiobutton.setOnClickListener {
                onClick(industry)
                selectedIndustry = industry
                notifyDataSetChanged()
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

    fun setSelectedIndustry(industry: FilterIndustryValue?) {
        selectedIndustry = industry
        notifyDataSetChanged()
    }
    fun clearSelection() {
        selectedIndustry = null
        notifyDataSetChanged()
        Log.d("IndustryAdapter", "clearSelection() called, adapter updated")

    }
}

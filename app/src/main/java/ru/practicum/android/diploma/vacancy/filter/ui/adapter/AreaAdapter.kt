package ru.practicum.android.diploma.vacancy.filter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.ItemAreaBinding
import ru.practicum.android.diploma.vacancy.filter.domain.model.Area

class AreaAdapter(
    private var areas: List<Area>,
    private val onClick: (Area) -> Unit
) : RecyclerView.Adapter<AreaAdapter. AreaViewHolder>() {

    inner class AreaViewHolder(
        private val binding: ItemAreaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(area: Area) {
            binding.areaNameTextview.text = area.name
            itemView.setOnClickListener {
                onClick(area)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val binding = ItemAreaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AreaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.bind(areas[position])
    }

    override fun getItemCount(): Int = areas.size

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }
}

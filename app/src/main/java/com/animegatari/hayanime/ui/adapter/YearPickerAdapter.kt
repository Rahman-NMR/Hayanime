package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ItemYearPickerBinding

class YearPickerAdapter(
    private val years: List<Int>,
    private val selectedYear: Int,
    private val onYearSelected: (Int) -> Unit,
) : RecyclerView.Adapter<YearPickerAdapter.YearViewHolder>() {
    inner class YearViewHolder(val binding: ItemYearPickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(year: Int, isActive: Boolean) {
            val backgroundResource = if (isActive) R.drawable.bg_corner_16 else 0
            val textColor = if (isActive) R.color.md_theme_surfaceVariant else R.color.text_color_secondary

            binding.tvYear.apply {
                setTextColor(itemView.context.getColor(textColor))
                setBackgroundResource(backgroundResource)
                text = year.toString()
            }
            binding.root.setOnClickListener { onYearSelected(year) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        val view = ItemYearPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YearViewHolder(view)
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        holder.bind(years[position], years[position] == selectedYear)
    }

    override fun getItemCount(): Int = years.size
}
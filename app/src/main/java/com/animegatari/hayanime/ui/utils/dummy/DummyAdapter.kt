package com.animegatari.hayanime.ui.utils.dummy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.databinding.LayoutAnimeGridBinding

class DummyAdapter : RecyclerView.Adapter<DummyAdapter.DummyaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyaViewHolder {
        val binding = LayoutAnimeGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DummyaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DummyaViewHolder, position: Int) {
        holder.bind(Dummy.dummyData[position])
    }

    override fun getItemCount() = Dummy.dummyData.size

    class DummyaViewHolder(private val binding: LayoutAnimeGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(str: String) {
            binding.title.text = str
        }
    }
}
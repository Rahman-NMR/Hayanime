package com.animegatari.hayanime.utils.dummy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.databinding.LayoutAnimeListBinding

class DummyAdapterList : RecyclerView.Adapter<DummyAdapterList.DummyaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyaViewHolder {
        val binding = LayoutAnimeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DummyaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DummyaViewHolder, position: Int) {
        holder.bind(Dummy.dummyData[position])
    }

    override fun getItemCount() = Dummy.dummyData.size

    class DummyaViewHolder(private val binding: LayoutAnimeListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(str: String) {
            binding.title.text = str
        }
    }
}
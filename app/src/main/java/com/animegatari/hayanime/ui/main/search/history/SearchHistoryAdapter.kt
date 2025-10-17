package com.animegatari.hayanime.ui.main.search.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.databinding.LayoutLatestSearchBinding
import com.animegatari.hayanime.domain.model.SearchHistoryItem

class SearchHistoryAdapter(
    private val onHistoryItemClicked: (String) -> Unit,
    private val onHistoryItemDeleted: (Long) -> Unit,
) : ListAdapter<SearchHistoryItem, SearchHistoryAdapter.SearchHistoryViewHolder>(SearchHistoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val binding = LayoutLatestSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val history = getItem(position)
        history.let { holder.bind(it, onHistoryItemClicked, onHistoryItemDeleted) }
    }

    class SearchHistoryViewHolder(private val binding: LayoutLatestSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchHistoryItem, onItemClicked: (String) -> Unit, onDeleteClicked: (Long) -> Unit) {
            binding.textHistory.text = item.queryText
            binding.root.setOnClickListener { onItemClicked(item.queryText) }
            binding.btnActionDelete.setOnClickListener { onDeleteClicked(item.id) }
        }
    }

    class SearchHistoryDiffCallback : DiffUtil.ItemCallback<SearchHistoryItem>() {
        override fun areItemsTheSame(oldItem: SearchHistoryItem, newItem: SearchHistoryItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SearchHistoryItem, newItem: SearchHistoryItem): Boolean = oldItem == newItem
    }
}
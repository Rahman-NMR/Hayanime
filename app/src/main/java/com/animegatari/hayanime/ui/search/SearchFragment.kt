package com.animegatari.hayanime.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.databinding.LayoutAnimeGridBinding
import com.animegatari.hayanime.ui.recyclerview.decorations.BottomPaddingItemDecoration
import kotlin.math.floor

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val root: View = binding.root
        binding.loadingIndicator.hide()

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            binding.loadingIndicator.apply {
                show()
                postDelayed({ hide() }, 11000)
            }
        }
        searchViewModel.text.observe(viewLifecycleOwner) {
//            binding.textSearch.text = it
        }

        binding.setupRecyclerView()

        return root
    }

    private val dummyData = listOf(
        "AVE",
        "The Adventures of Baron Munchausen",
        "Spirited Away from the Land of Gods",
        "Fairy Zero",
        "My Neighbor Totoro and the Magical Forest",
        "Transformer Legend of Cybertron: Rise of the Cybermen and Primeus Kingdom Cyber Luxuria Alicization",
        "Princess Mononoke's Epic Journey to Save Nature",
        "Howl's Moving Castle and the Sorcerer's Curse",
        "Narnia",
        "Grave of the Fireflies a Heartbreaking Tale of Survival",
        "The Wind Rises Dreams of Flight and Innovation",
        "Ponyo on the Cliff by the Sea a Magical Friendship",
    )

    fun calculateSpanCount(context: Context, columnWidthDp: Int): Int {
        if (columnWidthDp <= 0) return 1

        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

        val spanCount = floor(screenWidthDp / columnWidthDp).toInt()

        return if (spanCount > 0) spanCount else 1
    }

    private fun FragmentSearchBinding.setupRecyclerView() {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = object : RecyclerView.Adapter<DummyaViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyaViewHolder {
                val binding = LayoutAnimeGridBinding.inflate(layoutInflater, parent, false)
                return DummyaViewHolder(binding)
            }

            override fun onBindViewHolder(holder: DummyaViewHolder, position: Int) {
                holder.bind(dummyData[position])
            }

            override fun getItemCount() = dummyData.size
        }
    }

    class DummyaViewHolder(private val binding: LayoutAnimeGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(str: String) {
            binding.title.text = str
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.hide()
    }
}
package com.animegatari.hayanime.ui.main.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.ui.utils.layout.FabUtils.fabScrollBehavior

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            loadingIndicator.hide()
            fabScrollToTop.hide()

            val animeAdapter = adapter()

            setupInteractions(animeAdapter)
            handleSearchInput()
            setupRecyclerView(animeAdapter)
            adapterDataHandler(animeAdapter)
            handleLoadState(animeAdapter)
        }
    }

    private fun FragmentSearchBinding.setupInteractions(animeAdapter: AnimeGridAdapter) {
        recyclerView.addOnScrollListener(fabScrollBehavior(binding.fabScrollToTop))
        fabScrollToTop.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
        swipeRefresh.setOnRefreshListener {
            animeAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun FragmentSearchBinding.handleSearchInput() {
        searchView.setupWithSearchBar(binding.searchBar)
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            searchView.hide()
            searchBar.setText(binding.searchView.text.trim())

            val searchQuery = binding.searchView.text.toString()
            searchViewModel.getAnimeList(searchQuery)

            false
        }
    }

    private fun adapter(): AnimeGridAdapter = AnimeGridAdapter({ anime ->
        Toast.makeText(requireContext(), "TODO action ${anime.title}", Toast.LENGTH_SHORT).show()
    }, { anime ->
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "${BuildConfig.BASE_URL}/anime/${anime.id}".toUri()
        }
        startActivity(intent)
    })

    private fun FragmentSearchBinding.setupRecyclerView(animeAdapter: AnimeGridAdapter) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = animeAdapter
    }

    private fun adapterDataHandler(adapter: AnimeGridAdapter) = lifecycleScope.launch {
        searchViewModel.animeList.collectLatest { pagingData ->
            adapter.submitData(pagingData)
        }
    }

    private fun handleLoadState(animeAdapter: AnimeGridAdapter) = lifecycleScope.launch {
        animeAdapter.loadStateFlow.collectLatest { loadStates ->
            binding.loadingIndicator.isVisible = when (loadStates.refresh) {
                is LoadState.Loading -> true
                is LoadState.Error -> {
                    Toast.makeText(requireContext(), "Error loading anime", Toast.LENGTH_SHORT).show()
                    false
                }

                else -> false
            }
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
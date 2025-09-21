package com.animegatari.hayanime.ui.main.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.detail.EditOwnListBottomSheet
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.snackBarShort
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.FabUtils.attachFabScrollListener
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        val animeAdapter = animeAdapter()
        with(binding) {
            initializeViews()
            setupInteractions(animeAdapter)
            handleSearchInput()
            setupRecyclerView(animeAdapter)
            adapterDataHandler(animeAdapter)
            handleLoadState(animeAdapter)
        }
    }

    private fun FragmentSearchBinding.initializeViews() {
        attachFabScrollListener(recyclerView, fabScrollToTop)
        tvInfoMsg.text = getString(R.string.info_empty_initial_search)
        loadingIndicator.hide()
        fabScrollToTop.hide()
    }

    private fun FragmentSearchBinding.setupInteractions(animeAdapter: AnimeGridAdapter) {
        fabScrollToTop.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
        swipeRefresh.setOnRefreshListener {
            animeAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun FragmentSearchBinding.handleSearchInput() {
        searchView.setupWithSearchBar(binding.searchBar)
        searchView.editText.setOnEditorActionListener { searchItem, _, _ ->
            searchBar.setText(searchItem.text.trim())

            val searchQuery = searchItem.text.toString().trim()
            if (searchQuery.length >= 3) {
                searchView.hide()
                searchViewModel.getAnimeList(searchQuery)
                tvInfoMsg.text = getString(R.string.info_no_results_found, searchQuery)
                false
            } else {
                snackBarShort(root, getString(R.string.message_query_short))
                true
            }
        }
    }

    private fun animeAdapter(): AnimeGridAdapter = AnimeGridAdapter(
        onItemClicked = { anime ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "${BuildConfig.BASE_URL}anime/${anime.id}".toUri()
            }
            startActivity(intent)
        },
        onEditMyListClicked = { anime ->
            anime.id?.let { animeId ->
                val editOwnListSheet = EditOwnListBottomSheet.newInstance(animeId)
                editOwnListSheet.show(childFragmentManager, editOwnListSheet.tag)
            } ?: run {
                toastShort(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun FragmentSearchBinding.setupRecyclerView(animeAdapter: AnimeGridAdapter) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = animeAdapter
    }

    private fun adapterDataHandler(animeAdapter: AnimeGridAdapter) = lifecycleScope.launch {
        searchViewModel.animeList.collectLatest { pagingData ->
            animeAdapter.submitData(pagingData)
        }
    }

    private fun handleLoadState(animeAdapter: AnimeGridAdapter) = lifecycleScope.launch {
        animeAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            binding.tvInfoMsg.isVisible = refreshState is LoadState.NotLoading && animeAdapter.itemCount == 0
            binding.loadingIndicator.isVisible = when (refreshState) {
                is LoadState.Loading -> true
                is LoadState.Error -> {
                    toastShort(requireContext(), getString(R.string.message_error_occurred))
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
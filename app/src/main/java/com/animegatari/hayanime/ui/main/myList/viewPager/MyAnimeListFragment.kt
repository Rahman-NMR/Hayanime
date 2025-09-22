package com.animegatari.hayanime.ui.main.myList.viewPager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.FragmentMyAnimeListBinding
import com.animegatari.hayanime.ui.detail.EditOwnListBottomSheet
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.FabUtils.attachFabScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyAnimeListFragment : Fragment() {
    private var _binding: FragmentMyAnimeListBinding? = null
    private val binding get() = _binding!!

    private val myAnimeListViewModel: MyAnimeListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt(ARG_POSITION) ?: DEFAULT_POSITION
        val watchingStatusValue = getWatchingStatusValueFromPosition(position)
        val myListAdapter = initializeMyListAdapter()

        initializeViews(watchingStatusValue)
        setupInteractions(myListAdapter)
        setupRecyclerView(myListAdapter)

        observeViewModelStates(myListAdapter)
        handleLoadState(myListAdapter)
    }

    private fun getWatchingStatusValueFromPosition(position: Int): String? = when (position) {
        0 -> null
        1 -> WatchingStatus.WATCHING.apiValue
        2 -> WatchingStatus.COMPLETED.apiValue
        3 -> WatchingStatus.PLAN_TO_WATCH.apiValue
        4 -> WatchingStatus.ON_HOLD.apiValue
        5 -> WatchingStatus.DROPPED.apiValue
        else -> null
    }

    private fun initializeViews(watchingStatusValue: String?) = with(binding) {
        myAnimeListViewModel.getAnimeList(watchingStatusValue)

        val watchingStatusString = WatchingStatus.fromApiValue(watchingStatusValue).stringResId

        attachFabScrollListener(recyclerView, fabScrollToTop)
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(watchingStatusString))
        loadingIndicator.hide()
        fabScrollToTop.hide()
    }

    private fun setupInteractions(myListAdapter: MyListAdapter) = with(binding) {
        fabScrollToTop.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
        swipeRefresh.setOnRefreshListener {
            myListAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun initializeMyListAdapter(): MyListAdapter = MyListAdapter(
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
        },
        onAddProgressEpisode = {
            toastShort(requireContext(), "TODO action press again to add progress episode")
        }
    )

    private fun setupRecyclerView(myListAdapter: MyListAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = myListAdapter
    }

    private fun handleLoadState(myListAdapter: MyListAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        myListAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            binding.tvInfoMsg.isVisible = refreshState is LoadState.NotLoading && myListAdapter.itemCount == 0
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

    private fun observeViewModelStates(myListAdapter: MyListAdapter) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            myAnimeListViewModel.myAnimeList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { myListAdapter.submitData(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_POSITION = "position"
        private const val DEFAULT_POSITION = 0
        const val NUM_TABS = 6

        fun newInstance(position: Int) = MyAnimeListFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSITION, position) }
        }
    }
}
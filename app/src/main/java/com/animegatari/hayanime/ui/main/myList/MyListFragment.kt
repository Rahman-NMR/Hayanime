package com.animegatari.hayanime.ui.main.myList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.awaitNotLoading
import androidx.recyclerview.widget.LinearLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.FragmentMyListBinding
import com.animegatari.hayanime.ui.adapter.MyListAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.main.MainViewModel
import com.animegatari.hayanime.ui.main.ProfileMenuViewModel
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.extension.ProfileImage.loadProfileImage
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyListFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    private val myListViewModel: MyListViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileMenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myListAdapter = initializeMyListAdapter()

        setupAdapterRefreshListener(myListAdapter)
        initializeViews()
        setupInteractions(myListAdapter)
        setupRecyclerView(myListAdapter)

        observeViewModelStates(myListAdapter)
        observeLoadState(myListAdapter)
    }

    private fun setupAdapterRefreshListener(myListAdapter: MyListAdapter) {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resultDeleted = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resultDeleted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    myListAdapter.refresh()

                    if (resultUpdate) mainViewModel.showSnackbar(getString(R.string.message_anime_updated_successfully))
                    if (resultDeleted) mainViewModel.showSnackbar(getString(R.string.message_anime_deleted_successfully))
                }
            }
        }
    }

    private fun initializeViews() = with(binding) {
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(R.string.title_my_list))
        loadingIndicator.hide()
    }

    private fun setupInteractions(myListAdapter: MyListAdapter) = with(binding) {
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            handleChipGroupSelection(checkedIds)
            scrollToTopOnLoad(myListAdapter)
        }
        swipeRefresh.setOnRefreshListener {
            myListAdapter.refresh()
            scrollToTopOnLoad(myListAdapter)
            swipeRefresh.isRefreshing = false
        }
        toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleChipGroupSelection(checkedIds: List<Int>) = with(binding) {
        val selectedStatus = if (checkedIds.isNotEmpty()) {
            val checkedId = checkedIds.first()
            when (checkedId) {
                chipPlanToWatch.id -> WatchingStatus.PLAN_TO_WATCH.apiValue
                chipWatching.id -> WatchingStatus.WATCHING.apiValue
                chipCompleted.id -> WatchingStatus.COMPLETED.apiValue
                chipOnHold.id -> WatchingStatus.ON_HOLD.apiValue
                chipDropped.id -> WatchingStatus.DROPPED.apiValue
                else -> null
            }
        } else {
            null
        }

        val watchingStatusString = WatchingStatus.fromApiValue(selectedStatus).stringResId

        myListViewModel.getAnimeList(selectedStatus)
        tvInfoMsg.text = getString(R.string.info_no_results_found, getString(watchingStatusString))
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            showToast(requireContext(), "TODO go to profile")

            true
        }

        else -> false
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
                val action = MyListFragmentDirections.actionNavigationToNavigationEditAnime(
                    animeId = animeId,
                    requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
                )
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        },
        onAddProgressEpisode = { anime ->
            myListViewModel.updateAnimeProgress(
                animeId = anime.id,
                currentEpisodeProgress = anime.myListStatus?.numEpisodesWatched,
                numEpisode = anime.numEpisodes,
            )
        }
    )

    private fun setupRecyclerView(myListAdapter: MyListAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = myListAdapter
    }

    private fun loadProfileImage(imageUri: String?) = with(binding) {
        toolBar.menu.loadProfileImage(
            glide = Glide.with(requireContext()),
            lifecycle = viewLifecycleOwner.lifecycleScope,
            profilePictureUrl = imageUri,
            menuItemId = R.id.menu_item_avatar
        )
    }

    private fun scrollToTopOnLoad(myListAdapter: MyListAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        myListAdapter.loadStateFlow.awaitNotLoading()
        binding.recyclerView.scrollToPosition(0)
    }

    private fun observeLoadState(myListAdapter: MyListAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        myListAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            binding.tvInfoMsg.isVisible = refreshState is LoadState.NotLoading && myListAdapter.itemCount == 0
            binding.loadingIndicator.isVisible = when (refreshState) {
                is LoadState.Loading -> true
                is LoadState.Error -> {
                    showToast(requireContext(), getString(R.string.message_error_occurred))
                    false
                }

                else -> false
            }
        }
    }

    private fun observeViewModelStates(myListAdapter: MyListAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            myListViewModel.myAnimeList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { myListAdapter.submitData(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileImageUri
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { loadProfileImage(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            myListViewModel.events.collectLatest { event ->
                when (event) {
                    is MyListEvent.DataModified -> {
                        myListAdapter.refresh()
                        scrollToTopOnLoad(myListAdapter)
                    }

                    is MyListEvent.UpdateProgressError -> {
                        showToast(requireContext(), event.message ?: getString(R.string.message_error_occurred))
                    }
                }
            }
        }
    }

    override fun onReselected() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
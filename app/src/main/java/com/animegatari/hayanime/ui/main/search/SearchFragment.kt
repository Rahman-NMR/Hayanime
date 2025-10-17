package com.animegatari.hayanime.ui.main.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.base.ViewActionListener
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.main.ProfileMenuViewModel
import com.animegatari.hayanime.ui.main.search.history.SearchHistoryAdapter
import com.animegatari.hayanime.ui.main.search.history.SearchHistoryViewModel
import com.animegatari.hayanime.ui.profile.ProfileActivity
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.extension.ProfileImage.loadProfileImage
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showSnackbar
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@AndroidEntryPoint
class SearchFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()
    private val historyViewModel: SearchHistoryViewModel by viewModels()
    private val profileViewModel: ProfileMenuViewModel by activityViewModels()

    private var viewActionListener: ViewActionListener? = null
    private lateinit var searchViewBackCallback: OnBackPressedCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchViewBackCallback()
        setupSearchViewTransitionListener()

        val animeAdapter = initializeAnimeAdapter()
        val historyAdapter = initializeHistoryAdapter(animeAdapter)

        setupAdapterRefreshListener(animeAdapter)
        initializeViews()
        setupInteractions(animeAdapter)
        setupRecyclerView(animeAdapter, historyAdapter)
        handleSearchInput(animeAdapter)

        observeViewModelStates(animeAdapter, historyAdapter)
        observeLoadState(animeAdapter)
    }

    private fun setupAdapterRefreshListener(animeAdapter: AnimeGridAdapter) {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resultDeleted = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resultDeleted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    animeAdapter.refresh()

                    if (resultUpdate) {
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.message_anime_updated_successfully),
                            anchorView = requireActivity().findViewById(R.id.nav_view)
                        )
                    }
                    if (resultDeleted) {
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.message_anime_deleted_successfully),
                            anchorView = requireActivity().findViewById(R.id.nav_view)
                        )
                    }
                }
            }
        }
    }

    private fun initializeViews() = with(binding) {
        tvInfoMsg.text = getString(R.string.info_empty_initial_search)
    }

    private fun setupInteractions(animeAdapter: AnimeGridAdapter) = with(binding) {
        swipeRefresh.setOnRefreshListener {
            profileViewModel.getProfileImage()
            animeAdapter.refresh()
            scrollToTopOnLoad(animeAdapter)
        }
        searchBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_avatar -> {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
            true
        }

        else -> false
    }

    private fun handleSearchInput(animeAdapter: AnimeGridAdapter) = with(binding) {
        searchView.setupWithSearchBar(binding.searchBar)
        searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = textView.text.toString().trim()
                searchBar.setText(searchQuery)

                when {
                    searchQuery.length >= MIN_QUERY_LENGTH -> {
                        performSearch(animeAdapter, searchQuery)
                        historyViewModel.saveSearchQuery(searchQuery)
                    }

                    searchQuery.isEmpty() -> {
                        performSearch(animeAdapter)
                    }

                    else -> {
                        showSnackbar(root, getString(R.string.message_query_short))
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performSearch(animeAdapter: AnimeGridAdapter, query: String? = null) = with(binding) {
        searchView.hide()
        searchViewModel.getAnimeList(query ?: "")

        tvInfoMsg.text = if (query != null) {
            getString(R.string.info_no_results_found, query)
        } else {
            getString(R.string.info_empty_initial_search)
        }

        scrollToTopOnLoad(animeAdapter)
    }


    private fun initializeHistoryAdapter(animeAdapter: AnimeGridAdapter): SearchHistoryAdapter = SearchHistoryAdapter(
        onHistoryItemClicked = { query ->
            binding.searchBar.setText(query)
            binding.searchView.hide()

            searchViewModel.getAnimeList(query)
            historyViewModel.saveSearchQuery(query)

            scrollToTopOnLoad(animeAdapter)
        },
        onHistoryItemDeleted = { historyId ->
            historyViewModel.removeSelectedHistory(historyId)
        }
    )

    private fun initializeAnimeAdapter(): AnimeGridAdapter = AnimeGridAdapter(
        onItemClicked = { anime ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "${BuildConfig.BASE_URL}anime/${anime.id}".toUri()
            }
            startActivity(intent)
        },
        onEditMyListClicked = { anime ->
            anime.id?.let { animeId ->
                val action = SearchFragmentDirections.actionNavigationToNavigationEditAnime(
                    animeId = animeId,
                    requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
                )
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun setupRecyclerView(animeAdapter: AnimeGridAdapter, historyAdapter: SearchHistoryAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                calculateSpanCount(requireContext(), 200),
                StaggeredGridLayoutManager.VERTICAL
            )
            addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
            adapter = animeAdapter
        }

        recyclerViewLatestSearch.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
            adapter = historyAdapter
        }
    }

    private fun loadProfileImage(imageUri: String?) = with(binding) {
        searchBar.menu.loadProfileImage(
            glide = Glide.with(requireContext()),
            lifecycle = viewLifecycleOwner.lifecycleScope,
            profilePictureUrl = imageUri,
            menuItemId = R.id.menu_item_avatar
        )
    }

    private fun scrollToTopOnLoad(animeAdapter: AnimeGridAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        animeAdapter.loadStateFlow.awaitNotLoading()
        binding.recyclerView.scrollToPosition(0)
        binding.recyclerViewLatestSearch.smoothScrollToPosition(0)
    }

    private fun observeLoadState(animeAdapter: AnimeGridAdapter) = viewLifecycleOwner.lifecycleScope.launch {
        animeAdapter.loadStateFlow.collectLatest { loadStates ->
            val refreshState = loadStates.refresh

            val isListEmpty = animeAdapter.itemCount == 0
            binding.tvInfoMsg.isVisible = isListEmpty && (refreshState is LoadState.NotLoading || refreshState is LoadState.Error)
            binding.swipeRefresh.isRefreshing = refreshState is LoadState.Loading

            if (refreshState is LoadState.Error) {
                val message = when (refreshState.error) {
                    is ConnectException -> getString(R.string.message_failed_to_connect)
                    is SocketException -> getString(R.string.message_connection_lost)
                    is SocketTimeoutException -> getString(R.string.message_timeout)
                    is UnknownHostException -> getString(R.string.message_no_internet)
                    else -> getString(R.string.message_error_occurred)
                }

                showSnackbar(
                    view = binding.root,
                    message = message,
                    anchorView = requireActivity().findViewById(R.id.nav_view),
                    actionName = getString(R.string.action_retry),
                    action = { animeAdapter.retry() }
                )
            }
        }
    }

    private fun observeViewModelStates(animeAdapter: AnimeGridAdapter, historyAdapter: SearchHistoryAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.animeList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { animeAdapter.submitData(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileImageUri
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { loadProfileImage(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            historyViewModel.historyState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { historyAdapter.submitList(it) }
        }
    }

    private fun setupSearchViewBackCallback() {
        searchViewBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.searchView.isShowing) {
                    binding.searchView.hide()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, searchViewBackCallback)
    }

    private fun setupSearchViewTransitionListener() {
        binding.searchView.addTransitionListener { _, _, newState ->
            when (newState) {
                SearchView.TransitionState.SHOWN -> viewActionListener?.onViewShown()
                SearchView.TransitionState.HIDDEN -> viewActionListener?.onViewHidden()
                else -> Unit
            }
        }
    }

    override fun onReselected() {
        if (binding.recyclerView.computeVerticalScrollOffset() > 0) {
            binding.recyclerView.smoothScrollToPosition(0)
        } else {
            toggleSearchViewVisibility()
        }
    }

    private fun toggleSearchViewVisibility() = with(binding) {
        if (searchView.isShowing) {
            searchView.editText.clearFocus()
            searchView.hide()
        } else {
            searchView.show()
            searchView.editText.requestFocus()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewActionListener = context as? ViewActionListener
    }

    override fun onDetach() {
        super.onDetach()
        viewActionListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        _binding?.searchView?.hide()
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 3
    }
}
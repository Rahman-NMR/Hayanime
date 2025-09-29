package com.animegatari.hayanime.ui.main.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.FragmentSearchBinding
import com.animegatari.hayanime.ui.adapter.AnimeGridAdapter
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.base.ViewActionListener
import com.animegatari.hayanime.ui.detail.EditOwnListFragment
import com.animegatari.hayanime.ui.main.MainViewModel
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.ANIMATION_DURATION
import com.animegatari.hayanime.ui.utils.decorations.BottomPaddingItemDecoration
import com.animegatari.hayanime.ui.utils.layout.SpanCalculator.calculateSpanCount
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.snackBarShort
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), ReselectableFragment {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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

        setupAdapterRefreshListener(animeAdapter)
        initializeViews()
        setupInteractions(animeAdapter)
        setupRecyclerView(animeAdapter)
        handleSearchInput()

        observeViewModelStates(animeAdapter)
        handleLoadState(animeAdapter)
    }

    private fun setupAdapterRefreshListener(animeAdapter: AnimeGridAdapter) {
        parentFragmentManager.setFragmentResultListener(
            EditOwnListFragment.DETAIL_REQUEST_KEY,
            this
        ) { _, bundle ->
            val resultUpdate = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_UPDATED)
            val resulDelete = bundle.getBoolean(EditOwnListFragment.BUNDLE_KEY_DELETED)

            if (resultUpdate || resulDelete) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(ANIMATION_DURATION)
                    animeAdapter.refresh()

                    if (resultUpdate) mainViewModel.showSnackbar(getString(R.string.message_anime_updated_successfully))
                    if (resulDelete) mainViewModel.showSnackbar(getString(R.string.message_anime_deleted_successfully))
                }
            }
        }
    }

    private fun initializeViews() = with(binding) {
        tvInfoMsg.text = getString(R.string.info_empty_initial_search)
        loadingIndicator.hide()
    }

    private fun setupInteractions(animeAdapter: AnimeGridAdapter) = with(binding) {
        swipeRefresh.setOnRefreshListener {
            animeAdapter.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun handleSearchInput() = with(binding) {
        searchView.setupWithSearchBar(binding.searchBar)
        searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = textView.text.toString().trim()
                searchBar.setText(searchQuery)

                if (searchQuery.length >= MIN_QUERY_LENGTH) {
                    searchView.hide()
                    searchViewModel.getAnimeList(searchQuery)
                    tvInfoMsg.text = getString(R.string.info_no_results_found, searchQuery)
                } else {
                    snackBarShort(root, getString(R.string.message_query_short))
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

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
                toastShort(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun setupRecyclerView(animeAdapter: AnimeGridAdapter) = with(binding) {
        val paddingBottom = resources.getDimensionPixelSize(R.dimen.layout_padding_bottom)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            calculateSpanCount(requireContext(), 200),
            StaggeredGridLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(BottomPaddingItemDecoration(paddingBottom))
        recyclerView.adapter = animeAdapter
    }

    private fun handleLoadState(animeAdapter: AnimeGridAdapter) = viewLifecycleOwner.lifecycleScope.launch {
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

    private fun observeViewModelStates(animeAdapter: AnimeGridAdapter) = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.animeList
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { animeAdapter.submitData(it) }
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
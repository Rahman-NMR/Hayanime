package com.animegatari.hayanime.ui.main.myList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.local.datamodel.SortOption
import com.animegatari.hayanime.data.types.SortListUser
import com.animegatari.hayanime.databinding.BottomsheetMyListFilterBinding
import com.animegatari.hayanime.databinding.LayoutTextSelectionBinding
import com.animegatari.hayanime.ui.adapter.generic.CleanAdapter
import com.animegatari.hayanime.ui.adapter.generic.GenericDiffUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyListFilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomsheetMyListFilterBinding? = null
    private val binding get() = _binding!!

    private val myListViewModel: MyListViewModel by activityViewModels()
    private val sortAdapter by lazy { sortAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetMyListFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModelStates()
    }

    private fun setupRecyclerView() = with(binding.recyclerViewSort) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = sortAdapter
    }

    private fun sortAdapter() = CleanAdapter<SortOption, LayoutTextSelectionBinding>(
        inflater = LayoutTextSelectionBinding::inflate,
        binder = { vBinding, item ->
            vBinding.apply {
                textView.text = item.text
                icon.setImageResource(if (item.isSelected) R.drawable.ic_check_24px_rounded else 0)
                root.setOnClickListener {
                    myListViewModel.sortByValue(item.uniqueValue)
                    dismiss()
                }
            }
        }, diffCallback = GenericDiffUtil(
            onAreItemsTheSame = { old, new -> old.uniqueValue == new.uniqueValue },
            onAreContentsTheSame = { old, new -> old == new }
        )
    )

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myListViewModel.myAnimeListState.collectLatest { state ->
                        val sortLabels = SortListUser.entries.map {
                            SortOption(
                                text = getString(it.stringResId),
                                uniqueValue = it.apiValue,
                                isSelected = state.sort == it.apiValue
                            )
                        }
                        sortAdapter.submitList(sortLabels)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
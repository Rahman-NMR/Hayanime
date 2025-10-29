package com.animegatari.hayanime.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.local.datamodel.DateComponents
import com.animegatari.hayanime.data.model.AnimeMinimum
import com.animegatari.hayanime.data.model.MyListStatus
import com.animegatari.hayanime.data.types.AiringStatus
import com.animegatari.hayanime.data.types.RewatchPossibility
import com.animegatari.hayanime.data.types.WatchingPriority
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.FragmentEditOwnListBinding
import com.animegatari.hayanime.databinding.IncludeInputDateBinding
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.ui.adapter.NumberAdapter
import com.animegatari.hayanime.ui.dialog.YearPickerDialogFragment
import com.animegatari.hayanime.ui.utils.animation.ViewCollapseAnimation.collapseAnimated
import com.animegatari.hayanime.ui.utils.extension.AutoCompleteTextViewExtensions.setupDropdownWithEnum
import com.animegatari.hayanime.ui.utils.extension.AutoCompleteTextViewExtensions.setupSimpleDropdown
import com.animegatari.hayanime.ui.utils.interfaces.AlertDialog.confirmationDialog
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.handleTextChange
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.hideKeyboardAndClearFocus
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.scoreStringMap
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.shouldUpdateInputText
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showSnackbar
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.animegatari.hayanime.ui.utils.recyclerview.CenterSnapScaleScrollListener
import com.animegatari.hayanime.ui.utils.recyclerview.CenteredSnapHelper
import com.animegatari.hayanime.ui.utils.recyclerview.RecyclerViewUtils.applyHorizontalPadding
import com.animegatari.hayanime.ui.utils.recyclerview.RecyclerViewUtils.setupHorizontalList
import com.animegatari.hayanime.utils.DateInputUtils
import com.animegatari.hayanime.utils.TimeUtils
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditOwnListFragment : Fragment() {
    private var _binding: FragmentEditOwnListBinding? = null
    private val binding get() = _binding!!

    private val ownListViewModel: OwnListViewModel by viewModels()

    private val episodesSnapHelper: CenteredSnapHelper by lazy { CenteredSnapHelper() }
    private val scoreSnapHelper: CenteredSnapHelper by lazy { CenteredSnapHelper() }

    private var episodesScrollListener: CenterSnapScaleScrollListener? = null
    private var scoreScrollListener: CenterSnapScaleScrollListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupYearPickerListener()
    }

    private fun setupYearPickerListener() {
        setYearPickerListener(START_DATE_YEAR_REQUEST_KEY) { selectedYear ->
            ownListViewModel.saveStartDateYear(selectedYear.toString())
        }

        setYearPickerListener(FINISH_DATE_YEAR_REQUEST_KEY) { selectedYear ->
            ownListViewModel.saveFinishDateYear(selectedYear.toString())
        }
    }

    private fun setYearPickerListener(requestKey: String, onYearSelected: (Int) -> Unit) {
        childFragmentManager.setFragmentResultListener(requestKey, this) { _, bundle ->
            val selectedYear = bundle.getInt(YearPickerDialogFragment.BUNDLE_KEY_SELECTED_YEAR)
            onYearSelected(selectedYear)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditOwnListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadThisAnime()
        setupViews()
        observeViewModelStates()
    }

    private fun setupViews() {
        setupInteractionListeners()
        setupScoreRecyclerView()
        initializeStaticView()
    }

    private fun initializeStaticView() = with(binding) {
        btnActionSave.shrink()
        startDate.labelTitleDate.text = getString(R.string.label_start_date)
        finishDate.labelTitleDate.text = getString(R.string.label_finish_date)
        progress.apply {
            title.text = getString(R.string.label_progress)
            extraTitle.isVisible = true
        }
        score.apply {
            title.text = getString(R.string.label_score)
            itemLabel.isVisible = true
        }
    }

    private fun setupInteractionListeners() = with(binding) {
        toolBar.setNavigationOnClickListener { dismiss() }
        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete_anime -> openDeleteConfirmationDialog()
                else -> false
            }
        }
        btnActionSave.setOnClickListener { saveChanges() }
        btnActionExpand.setOnClickListener { toggleAdvancedOptions() }
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds -> handleChipGroupSelection(checkedIds) }
        swipeRefresh.setOnRefreshListener { loadThisAnime() }

        setupDateInputListeners(
            dateBinding = startDate,
            onTodayDateClick = { todayDate -> ownListViewModel.updateStartDate(todayDate) },
            onUnknownDateChecked = { isUnknown -> ownListViewModel.updateStartDateUnknown(isUnknown) })
        setupDateInputListeners(
            dateBinding = finishDate,
            onTodayDateClick = { todayDate -> ownListViewModel.updateFinishDate(todayDate) },
            onUnknownDateChecked = { isUnknown -> ownListViewModel.updateFinishDateUnknown(isUnknown) })
        setupExtraFieldInteraction()
    }

    private fun setupDateInputListeners(
        dateBinding: IncludeInputDateBinding,
        onTodayDateClick: (String) -> Unit,
        onUnknownDateChecked: (Boolean) -> Unit,
    ) = with(dateBinding) {
        btnTodayDate.setOnClickListener { onTodayDateClick(setDateToToday()) }
        checkUnknownDate.setOnCheckedChangeListener { _, isChecked ->
            handleDateUnknownToggle(this, isChecked)
            onUnknownDateChecked(isChecked)
        }
    }

    private fun setupExtraFieldInteraction() = with(binding.extraFields) {
        switchRewatching.setOnCheckedChangeListener { _, isChecked -> handleRewatchToggle(isChecked) }
        totalRewatchInputText.doAfterTextChanged { handleTextChange(it) { text -> ownListViewModel.updateRewatchedCount(text) } }
        commentsInputText.doAfterTextChanged { handleTextChange(it) { text -> ownListViewModel.updateComments(text) } }
        tagsInputText.doAfterTextChanged { handleTextChange(it) { text -> ownListViewModel.updateTags(text) } }
    }

    private fun saveChanges() = with(binding.btnActionSave) {
        hideKeyboardAndClearFocus(requireView())
        if (isExtended) {
            ownListViewModel.saveChanges { response ->
                response.onSuccess { isSaved ->
                    if (isSaved == true) {
                        parentFragmentManager.setFragmentResult(ownListViewModel.requestKey, bundleOf(BUNDLE_KEY_UPDATED to true))
                        dismiss()
                    } else {
                        showSnackbar(requireView(), getString(R.string.message_same_data))
                    }
                }
                response.onError { message ->
                    showSnackbar(requireView(), message ?: getString(R.string.message_failed_save_changes))
                }
            }
        } else {
            extend()
            lifecycleScope.launch {
                delay(1234)
                if (isAdded && isExtended) {
                    shrink()
                }
            }
        }
    }

    private fun openDeleteConfirmationDialog(): Boolean {
        confirmationDialog(
            context = requireContext(),
            title = getString(R.string.title_delete_anime_confirmation),
            message = getString(R.string.message_delete_anime_confirmation),
            positiveButton = getString(R.string.action_delete),
            negativeButton = getString(R.string.label_cancel)
        ) {
            ownListViewModel.deleteThisSeries { response ->
                response.onSuccess {
                    parentFragmentManager.setFragmentResult(ownListViewModel.requestKey, bundleOf(BUNDLE_KEY_DELETED to true))
                    dismiss()
                }
                response.onError { message ->
                    showSnackbar(requireView(), message ?: getString(R.string.message_failed_delete_anime))
                }
            }
        }

        return true
    }

    private fun toggleAdvancedOptions() = with(binding) {
        val targetView = extraFields.root
        if (targetView.isVisible) {
            targetView.collapseAnimated {
                updateExpandButtonState(R.string.label_show_advanced, R.drawable.ic_keyboard_arrow_down_24px_rounded)
            }
        } else {
            targetView.isVisible = true
            updateExpandButtonState(R.string.label_hide_advanced, R.drawable.ic_keyboard_arrow_up_24px_rounded)
        }
    }

    private fun updateExpandButtonState(textResId: Int, drawableResId: Int) = with(binding.btnActionExpand) {
        text = getString(textResId)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0)
    }

    private fun handleRewatchToggle(isChecked: Boolean) = with(binding.extraFields) {
        ownListViewModel.updateIsRewatching(isChecked)
        flowTotalRewatch.isVisible = isChecked
    }

    private fun setDateToToday(): String {
        val currentYear = TimeUtils.getCurrentYear().toString()
        val currentMonth = TimeUtils.getCurrentMonth().toString().padStart(2, '0')
        val currentDay = TimeUtils.getCurrentDay().toString().padStart(2, '0')

        return "$currentYear-$currentMonth-$currentDay"
    }

    private fun handleDateUnknownToggle(dateBinding: IncludeInputDateBinding, isChecked: Boolean) = with(dateBinding) {
        layoutYear.isEnabled = !isChecked
        layoutMonth.isEnabled = !isChecked
        layoutDay.isEnabled = !isChecked
        btnTodayDate.isEnabled = !isChecked
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

        ownListViewModel.updateWatchingStatus(selectedStatus)
        if (selectedStatus == WatchingStatus.COMPLETED.apiValue) {
            ownListViewModel.forceUpdateEpisodesWatched(selectedStatus)
        }
    }

    private fun setupCenteringRecyclerView(
        recyclerView: RecyclerView,
        maxitems: Int,
        snapHelper: CenteredSnapHelper,
        labelTextView: TextView? = null,
        labelMap: Map<Int, String>? = null,
        onItemSelected: (Int) -> Unit,
    ): CenterSnapScaleScrollListener {
        val itemRange = (0..maxitems).toList()
        val numberAdapter = NumberAdapter()
        val scrollListener = CenterSnapScaleScrollListener(
            snapHelper = snapHelper,
            getItemValue = { position -> itemRange.getOrNull(position) },
            onItemSelected = onItemSelected,
            currentItemLabelTextView = labelTextView,
            valueToLabelMap = labelMap
        )

        recyclerView.setupHorizontalList(
            adapter = numberAdapter,
            snapHelper = snapHelper
        )
        scrollListener.attach(recyclerView)

        numberAdapter.submitList(itemRange)
        return scrollListener
    }

    private fun setupEpisodesRecyclerView(maxEpisodesValue: Int?) = with(binding) {
        val maxEpisodeCount = maxEpisodesValue?.takeIf { it > 0 } ?: UShort.MAX_VALUE.toInt()

        episodesScrollListener = setupCenteringRecyclerView(
            recyclerView = progress.recyclerView,
            maxitems = maxEpisodeCount,
            snapHelper = episodesSnapHelper,
        ) { selectedEpisode ->
            ownListViewModel.updateSelectedEpisode(selectedEpisode)
        }
    }

    private fun setupScoreRecyclerView() = with(binding) {
        val scoreLabels = requireContext().scoreStringMap()

        scoreScrollListener = setupCenteringRecyclerView(
            recyclerView = score.recyclerView,
            maxitems = scoreLabels.size - 1,
            snapHelper = scoreSnapHelper,
            labelTextView = score.itemLabel,
            labelMap = scoreLabels,
        ) { selectedScore ->
            ownListViewModel.updateSelectedScore(selectedScore)
        }
    }

    private fun findChipForStatus(status: String?): Chip? = with(binding) {
        return when (status) {
            WatchingStatus.WATCHING.apiValue -> chipWatching
            WatchingStatus.COMPLETED.apiValue -> chipCompleted
            WatchingStatus.ON_HOLD.apiValue -> chipOnHold
            WatchingStatus.DROPPED.apiValue -> chipDropped
            WatchingStatus.PLAN_TO_WATCH.apiValue -> chipPlanToWatch
            else -> null
        }
    }

    private fun setDateInputFields(
        dateBinding: IncludeInputDateBinding,
        dateComponents: DateComponents?,
        title: String,
        requestKey: String,
        onMonth: (String?) -> Unit,
        onDay: (String?) -> Unit,
    ) = with(dateBinding) {
        val dialogTitle = getString(R.string.title_choose_x_year, title)
        val defaultLabel = getString(R.string.label_select)

        val initialYear = dateComponents?.year
        val initialMonth = dateComponents?.month
        val initialDay = dateComponents?.day

        layoutMonth.isEnabled = initialYear != null
        layoutDay.isEnabled = initialMonth != null && initialYear != null

        val showYearDialogAction = {
            val dialog = YearPickerDialogFragment.newInstance(
                initialYear = initialYear?.toInt() ?: 0,
                dialogTitle = dialogTitle,
                requestKey = requestKey
            )
            dialog.show(childFragmentManager, dialog.tag)
        }

        yearInput.setText(initialYear ?: defaultLabel)
        yearInput.setOnClickListener { showYearDialogAction() }
        layoutYear.setEndIconOnClickListener { showYearDialogAction() }
        monthInput.setupSimpleDropdown(
            items = DateInputUtils.getAllMonthDisplayValues(),
            defaultDisplayValue = DateInputUtils.getMonthDisplayValue(initialMonth) ?: defaultLabel,
            onItemSelected = { selectedMonth ->
                val monthValue = DateInputUtils.getMonthApiValue(selectedMonth).takeIf { it != "00" }
                onMonth(monthValue)
            }
        )
        dayInput.setupSimpleDropdown(
            items = DateInputUtils.getAllDayDisplayValues(),
            defaultDisplayValue = DateInputUtils.getDayDisplayValue(initialDay) ?: defaultLabel,
            onItemSelected = { selectedDay ->
                val dayValue = DateInputUtils.getDayApiValue(selectedDay).takeIf { it != "00" }
                onDay(dayValue)
            }
        )
    }

    private fun setupExtraFieldsViews(myListStatus: MyListStatus?) = with(binding.extraFields) {
        switchRewatching.isChecked = myListStatus?.isRewatching ?: false

        val rewatchCount = myListStatus?.numTimesRewatched?.takeIf { it > 0 }?.toString() ?: ""
        if (shouldUpdateInputText(totalRewatchInputText, rewatchCount)) {
            totalRewatchInputText.setText(rewatchCount)
        }

        val tags = myListStatus?.tags?.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: ""
        if (shouldUpdateInputText(tagsInputText, tags)) {
            tagsInputText.setText(tags)
        }

        val comments = myListStatus?.comments?.takeIf { it.isNotBlank() } ?: ""
        if (shouldUpdateInputText(commentsInputText, comments)) {
            commentsInputText.setText(myListStatus?.comments)
        }

        setupExtraFieldDropdowns(myListStatus)
    }

    private fun setupExtraFieldDropdowns(myListStatus: MyListStatus?) = with(binding.extraFields) {
        dropdownWatchPriority.setupDropdownWithEnum(
            items = WatchingPriority.getDisplayableValues(),
            defaultItem = WatchingPriority.fromApiValue(myListStatus?.priority),
            onItemSelected = { selectedValue ->
                ownListViewModel.updateWatchingPriority(selectedValue.apiValue)
            }
        )
        dropdownPossibilityRewatch.setupDropdownWithEnum(
            items = RewatchPossibility.getDisplayableValues(),
            defaultItem = RewatchPossibility.fromApiValue(myListStatus?.rewatchValue),
            onItemSelected = { selectedValue ->
                ownListViewModel.updateRewatchPossibility(selectedValue.apiValue)
            }
        )
    }

    private fun updateMyListStatusViews(myListStatus: MyListStatus?, anime: AnimeMinimum?) = with(binding) {
        val itemWidthPx = resources.getDimensionPixelSize(R.dimen.item_rv_scrolling_width)
        val paddingView = resources.getDimensionPixelSize(R.dimen.normal_dp) * 2

        val numEpisodesWatched = myListStatus?.numEpisodesWatched ?: 0
        val stringTotalEpisode = anime?.numEpisodes?.takeIf { it > 0 }?.toString() ?: getString(R.string.unknown_symbol)
        progress.extraTitle.text = getString(R.string.label_num_episodes, "$numEpisodesWatched/$stringTotalEpisode")

        progress.recyclerView.post {
            progress.recyclerView.applyHorizontalPadding(numEpisodesWatched, root.width, itemWidthPx, paddingView)
        }

        val currentScore = myListStatus?.score ?: 0
        score.recyclerView.post {
            score.recyclerView.applyHorizontalPadding(currentScore, root.width, itemWidthPx, paddingView)
        }

        setupExtraFieldsViews(myListStatus)

        val status = myListStatus?.status
        val chipToCheck = status?.let { findChipForStatus(it) }
        chipToCheck?.let { it.isChecked = true } ?: run {
            chipGroup.clearCheck()
        }
    }

    private fun startDateObserver(valueStartDate: DateComponents?) = with(binding) {
        setDateInputFields(
            dateBinding = startDate,
            dateComponents = valueStartDate,
            title = getString(R.string.label_start_date),
            requestKey = START_DATE_YEAR_REQUEST_KEY,
            onMonth = { ownListViewModel.saveStartDateMonth(it) },
            onDay = { ownListViewModel.saveStartDateDay(it) })
    }

    private fun finishDateObserver(valuefinishDate: DateComponents?) = with(binding) {
        setDateInputFields(
            dateBinding = finishDate,
            dateComponents = valuefinishDate,
            title = getString(R.string.label_finish_date),
            requestKey = FINISH_DATE_YEAR_REQUEST_KEY,
            onMonth = { ownListViewModel.saveFinishDateMonth(it) },
            onDay = { ownListViewModel.saveFinishDateDay(it) })
    }

    private fun observeUIState(anime: AnimeMinimum?) = with(binding) {
        if (!isAdded || _binding == null) return

        animeTitle.text = anime?.title ?: getString(R.string.label_unknown)
        airingStatus.text = getString(AiringStatus.fromApiValue(anime?.status).stringResId)

        val myListStatus = anime?.myListStatus
        updateMyListStatusViews(myListStatus, anime)
    }

    private fun updateLoadingState(isLoading: Boolean) = with(binding) {
        if (!isAdded || _binding == null) return

        swipeRefresh.isRefreshing = isLoading
        nestedScrollView.isVisible = !isLoading
        btnActionSave.isEnabled = !isLoading
        toolBar.menu.findItem(R.id.menu_delete_anime)?.isEnabled = !isLoading
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { ownListViewModel.isLoading.collectLatest(::updateLoadingState) }
                launch { ownListViewModel.animeUIState.collectLatest(::observeUIState) }
                launch { ownListViewModel.maxEpisodes.collectLatest(::setupEpisodesRecyclerView) }
                launch { ownListViewModel.startDateComponents.collectLatest(::startDateObserver) }
                launch { ownListViewModel.finishDateComponents.collectLatest(::finishDateObserver) }
            }
        }
    }

    private fun loadThisAnime() {
        ownListViewModel.loadMyAnimeDetail { response ->
            response.onError {
                showToast(requireContext(), getString(R.string.message_failed_load_data))
                dismiss()
            }
        }
    }

    private fun dismiss() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        episodesScrollListener?.detach(binding.progress.recyclerView)
        scoreScrollListener?.detach(binding.score.recyclerView)
        episodesSnapHelper.attachToRecyclerView(null)
        scoreSnapHelper.attachToRecyclerView(null)
        _binding = null
    }

    companion object {
        const val DETAIL_REQUEST_KEY = "DETAIL_REQUEST_KEY"
        const val BUNDLE_KEY_DELETED = "BUNDLE_KEY_DELETED"
        const val BUNDLE_KEY_UPDATED = "BUNDLE_KEY_UPDATED"

        const val START_DATE_YEAR_REQUEST_KEY = "START_DATE_YEAR_REQUEST"
        const val FINISH_DATE_YEAR_REQUEST_KEY = "FINISH_DATE_YEAR_REQUEST"
    }
}
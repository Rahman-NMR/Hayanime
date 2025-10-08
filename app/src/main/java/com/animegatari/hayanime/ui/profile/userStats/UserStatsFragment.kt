package com.animegatari.hayanime.ui.profile.userStats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.types.Gender
import com.animegatari.hayanime.databinding.FragmentUserStatsBinding
import com.animegatari.hayanime.utils.FormatterUtils.formatApiDate
import com.animegatari.hayanime.utils.FormatterUtils.formattedDateTimeZone
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserStatsFragment : Fragment() {
    private var _binding: FragmentUserStatsBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserStatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userInfo
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { userInfo ->
                    Glide.with(requireContext())
                        .load(userInfo?.picture)
                        .into(binding.userPicture)
                    binding.userName.text = userInfo?.name?.takeIf { it.isBlank().not() } ?: getString(R.string.label_unknown)

                    binding.userLocation.isVisible = userInfo?.location.isNullOrBlank().not()
                    binding.userLocation.text = userInfo?.location

                    binding.userGender.setImageResource(Gender.fromApiValue(userInfo?.gender).iconResId)

                    binding.birthDate.text = formatApiDate(userInfo?.birthday) ?: getString(R.string.nothing)
                    binding.joinDate.text = formattedDateTimeZone(
                        dateStr = userInfo?.joinedAt,
                        timeZoneStr = userInfo?.timeZone
                    ) ?: getString(R.string.nothing)
                    binding.timezone.text = userInfo?.timeZone?.takeIf { it.isBlank().not() } ?: getString(R.string.nothing)
                    binding.supporterStatus.text = when (userInfo?.isSupporter) {
                        true -> getString(R.string.label_supporter)
                        false -> getString(R.string.label_not_supporter)
                        null -> getString(R.string.nothing)
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
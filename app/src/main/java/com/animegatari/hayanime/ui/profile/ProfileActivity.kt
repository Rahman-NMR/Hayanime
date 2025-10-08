package com.animegatari.hayanime.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_profile) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolBar.title = destination.label

            if (destination.id == R.id.navigation_user_stats) {
                binding.toolBar.setNavigationIcon(R.drawable.ic_close_24px_rounded)
                binding.toolBar.inflateMenu(R.menu.profile_menu)
                binding.toolBar.setNavigationOnClickListener { finish() }
            } else {
                binding.toolBar.menu.clear()
                binding.toolBar.setNavigationIcon(R.drawable.ic_arrow_back_24px_rounded)
                binding.toolBar.setNavigationOnClickListener { navController.navigateUp() }
            }
        }

        binding.toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_settings -> {
                    navController.navigate(R.id.action_userStatsFragment_to_settingsFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.animegatari.hayanime.ui.profile

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
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

        setupToolbar(navController)
        setupDestinationChangeListener(navController)
    }

    private fun setupToolbar(navController: NavController) {
        binding.toolBar.setOnMenuItemClickListener { item -> handleMenuItemClick(item, navController) }
    }

    private fun setupDestinationChangeListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ -> updateToolbarForDestination(destination, navController) }
    }

    private fun handleMenuItemClick(item: MenuItem?, navController: NavController): Boolean = when (item?.itemId) {
        R.id.menu_item_settings -> {
            navController.navigate(R.id.action_userStatsFragment_to_settingsFragment)
            true
        }

        else -> false
    }

    private fun updateToolbarForDestination(destination: NavDestination, navController: NavController) {
        binding.toolBar.title = destination.label
        when (destination.id) {
            R.id.navigation_user_stats -> configureToolbarForUserStats()
            else -> configureToolbarForSubscreen(navController)
        }
    }

    private fun configureToolbarForUserStats() = with(binding.toolBar) {
        menu.clear()
        inflateMenu(R.menu.profile_menu)

        setNavigationIcon(R.drawable.ic_close_24px_rounded)
        setNavigationOnClickListener { finish() }
    }

    private fun configureToolbarForSubscreen(navController: NavController) = with(binding.toolBar) {
        menu.clear()

        setNavigationIcon(R.drawable.ic_arrow_back_24px_rounded)
        setNavigationOnClickListener { navController.navigateUp() }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
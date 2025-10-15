package com.animegatari.hayanime.ui.profile

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ActivityProfileBinding
import com.animegatari.hayanime.ui.auth.AuthViewModel
import com.animegatari.hayanime.ui.utils.interfaces.AlertDialog.confirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_profile) as NavHostFragment
        val navController = navHostFragment.navController

        setupToolbar(navController)
        setupDestinationChangeListener(navController)
        observeAuthState()
    }

    private fun setupToolbar(navController: NavController) {
        binding.toolBar.setOnMenuItemClickListener { item -> handleMenuItemClick(item, navController) }
    }

    private fun setupDestinationChangeListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ -> updateToolbarForDestination(destination, navController) }
    }

    private fun handleMenuItemClick(item: MenuItem?, navController: NavController): Boolean = when (item?.itemId) {
        R.id.menu_item_logout -> openConfirmationDialog()
        R.id.menu_item_settings -> {
            navController.navigate(R.id.action_userStatsFragment_to_settingsFragment)
            true
        }

        else -> false
    }

    private fun openConfirmationDialog(): Boolean {
        confirmationDialog(
            context = this@ProfileActivity,
            title = getString(R.string.title_logout_confirmation),
            message = getString(R.string.message_logout_confirmation),
            positiveButton = getString(R.string.action_logout),
            negativeButton = getString(R.string.label_cancel)
        ) {
            authViewModel.logout()
        }

        return true
    }

    private fun updateToolbarForDestination(destination: NavDestination, navController: NavController) {
        binding.toolBar.title = destination.label
        when (destination.id) {
            R.id.navigation_user_stats -> configureToolbarForUserStats()
            else -> configureToolbarForSubscreen(navController)
        }
    }

    private fun configureToolbarForUserStats() = with(binding.toolBar) {
        setNavigationIcon(R.drawable.avd_back_to_close_24)
        (navigationIcon as? AnimatedVectorDrawable)?.start()

        menu.findItem(R.id.menu_item_settings)?.apply {
            setIcon(R.drawable.avd_settings_shown)
            (icon as? AnimatedVectorDrawable)?.start()
            isEnabled = true
        }

        setNavigationOnClickListener { finish() }
    }

    private fun configureToolbarForSubscreen(navController: NavController) = with(binding.toolBar) {
        setNavigationIcon(R.drawable.avd_close_to_back_24)
        (navigationIcon as? AnimatedVectorDrawable)?.start()

        menu.findItem(R.id.menu_item_settings)?.apply {
            setIcon(R.drawable.avd_settings_hidden)
            (icon as? AnimatedVectorDrawable)?.start()
            isEnabled = false
        }

        setNavigationOnClickListener { navController.navigateUp() }
    }

    private fun observeAuthState() = lifecycleScope.launch {
        authViewModel.isLoggedIn.collectLatest { isLoggedIn ->
            if (isLoggedIn == false) {
                finish()
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
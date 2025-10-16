package com.animegatari.hayanime.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ActivityMainBinding
import com.animegatari.hayanime.ui.auth.AuthActivity
import com.animegatari.hayanime.ui.auth.AuthViewModel
import com.animegatari.hayanime.ui.base.ReselectableFragment
import com.animegatari.hayanime.ui.base.ViewActionListener
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.animateSlideDownAndHide
import com.animegatari.hayanime.ui.utils.animation.ViewSlideInOutAnimation.animateSlideUpAndShow
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ViewActionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var backPressedTime: Long = 0

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this@MainActivity, onBackHandler)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        setupDestinationChangedListener()
        setupReselectBottomNavListener(navHostFragment)
        observeLoginStatus()
    }

    override fun onViewShown() {
        binding.navView.animateSlideDownAndHide()
        onBackHandler.isEnabled = false
    }

    override fun onViewHidden() {
        binding.navView.animateSlideUpAndShow()
        onBackHandler.isEnabled = true
    }

    private fun setupDestinationChangedListener() = with(binding) {
        val mainDestinations = setOf(R.id.navigation_season, R.id.navigation_search, R.id.navigation_my_list)
        var previousDestinationId: Int? = null

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentDestinationId = destination.id
            if (destination.id in mainDestinations) {
                if (previousDestinationId !in mainDestinations) {
                    navView.animateSlideUpAndShow()
                }
            } else {
                navView.animateSlideDownAndHide()
            }
            previousDestinationId = currentDestinationId
        }
    }

    private fun setupReselectBottomNavListener(navHostFragment: NavHostFragment) = with(binding) {
        navView.setOnItemReselectedListener { item ->
            val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
            val mainDestinations = setOf(R.id.navigation_season, R.id.navigation_search, R.id.navigation_my_list)

            if (item.itemId in mainDestinations && currentFragment != null) {
                if (currentFragment is ReselectableFragment) {
                    currentFragment.onReselected()
                }
            }
        }
    }

    private val onBackHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            when (navController.currentDestination?.id) {
                R.id.navigation_search -> handleBackPressedOnSearch()
                else -> navController.navigateUp()
            }
        }

        private fun handleBackPressedOnSearch() {
            if (backPressedTime + BACK_PRESS_EXIT_DELAY > System.currentTimeMillis()) {
                finish()
            } else {
                showToast(this@MainActivity, getString(R.string.message_back_pressed), BACK_PRESS_EXIT_DELAY.toInt())
                backPressedTime = System.currentTimeMillis()
            }
        }
    }

    private fun observeLoginStatus() = lifecycleScope.launch {
        authViewModel.isLoggedIn.collectLatest { isLoggedIn ->
            if (isLoggedIn == false) {
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.validateToken()
    }

    companion object {
        private const val BACK_PRESS_EXIT_DELAY = 2000L
    }
}
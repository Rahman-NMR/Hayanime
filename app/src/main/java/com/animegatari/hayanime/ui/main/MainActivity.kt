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
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.toastShort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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

        observeLoginStatus()
    }

    private val onBackHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (navController.currentDestination?.id == R.id.navigation_search) {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish()
                } else {
                    toastShort(this@MainActivity, getString(R.string.message_back_pressed))
                }

                backPressedTime = System.currentTimeMillis()
            } else navController.navigateUp()
        }
    }

    private fun observeLoginStatus() = lifecycleScope.launch {
        authViewModel.isLoggedIn.collect { isLoggedIn ->
            if (isLoggedIn == false) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.validateToken()
    }
}
package com.animegatari.hayanime.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var backPressedTime: Long = 0
    private val onBackInvokedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (navController.currentDestination?.id == R.id.navigation_search) {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.toast_back_pressed), Toast.LENGTH_SHORT).show()
                }

                backPressedTime = System.currentTimeMillis()
            } else navController.navigateUp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this@MainActivity, onBackInvokedCallback)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)
    }
}
package com.animegatari.hayanime.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.animegatari.hayanime.BuildConfig
import com.animegatari.hayanime.R
import com.animegatari.hayanime.core.PKCEUtil.generateCodeVerifier
import com.animegatari.hayanime.databinding.ActivityAuthBinding
import com.animegatari.hayanime.ui.main.MainActivity
import com.animegatari.hayanime.ui.utils.PopupMessage.toastShort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private var keepOnSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepOnSplash }
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)

        observeLoginStatus()
        observeLoadingStatus()
        onBackPressedDispatcher.addCallback(this@AuthActivity, onBackHandler)

        binding.btnAuthAction.setOnClickListener { startOAuthFlow() }
    }

    private val onBackHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    private fun observeLoadingStatus() = lifecycleScope.launch {
        authViewModel.isLoading.collect { isLoading ->
            binding.progressIndicator.isVisible = isLoading
            binding.btnAuthAction.isEnabled = !isLoading
        }
    }

    private fun observeLoginStatus() = lifecycleScope.launch {
        authViewModel.isLoggedIn.collect { isLoggedIn ->
            keepOnSplash = when (isLoggedIn) {
                true -> {
                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                    false
                }

                false -> {
                    setContentView(binding.root)
                    false
                }

                else -> true
            }
        }
    }

    private fun startOAuthFlow() {
        val codeVerifier = generateCodeVerifier()
        authViewModel.saveCodeVerifier(codeVerifier)

        val codeChallenge = codeVerifier
        val customTabsIntent = CustomTabsIntent.Builder().build()

        customTabsIntent.launchUrl(this, authViewModel.authUrl(codeChallenge))
    }

    private fun handleRedirect(intent: Intent) {
        val data: Uri? = intent.data

        if (data != null && data.scheme == BuildConfig.APPLICATION_ID && data.host == "callback") {
            val code = data.getQueryParameter("code")

            authViewModel.handleAuthCode(code)
        } else {
            toastShort(this, getString(R.string.message_error_occurred))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleRedirect(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
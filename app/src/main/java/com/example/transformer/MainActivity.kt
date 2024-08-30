package com.example.transformer

import AppPreferences
import OnboardingScreen
import SplashScreenContent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.transformer.ui.theme.MotionLayoutWithNestedScrollAndSwipeableTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var keepSplashScreenOn by mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreenOn }

        enableEdgeToEdge()
        appPreferences = AppPreferences(this)

        setContent {
            MotionLayoutWithNestedScrollAndSwipeableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showOnboarding by remember { mutableStateOf(appPreferences.isFirstLaunch) }
                    var isLoading by remember { mutableStateOf(true) }

                    LaunchedEffect(key1 = true) {
                        delay(2000) // Show splash screen for 2 seconds
                        isLoading = false
                        keepSplashScreenOn = false
                    }

                    when {
                        isLoading -> {
                            SplashScreenContent()

                        }

                        showOnboarding -> OnboardingScreen {
                            showOnboarding = false
                            appPreferences.isFirstLaunch = false
                        }
                        else -> MainScreen()
                    }
                }
            }
        }
    }
}
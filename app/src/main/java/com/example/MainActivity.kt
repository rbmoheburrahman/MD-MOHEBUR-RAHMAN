package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainContainer
import com.example.ui.screens.SignupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.FeedViewModel

enum class AppScreen {
    SPLASH,
    LOGIN,
    SIGNUP,
    MAIN_CONTAINER
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val authViewModel: AuthViewModel = viewModel()
                val feedViewModel: FeedViewModel = viewModel()

                var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
                val currentUser by authViewModel.currentUser.collectAsState()

                // React to auth session changes
                LaunchedEffect(currentUser) {
                    if (currentScreen != AppScreen.SPLASH) {
                        if (currentUser != null) {
                            currentScreen = AppScreen.MAIN_CONTAINER
                        } else {
                            currentScreen = AppScreen.LOGIN
                        }
                    }
                }

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn() with fadeOut()
                    },
                    label = "AppScreenTransitions",
                    modifier = Modifier.fillMaxSize()
                ) { screen ->
                    when (screen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onProgressFinished = {
                                    if (currentUser != null) {
                                        currentScreen = AppScreen.MAIN_CONTAINER
                                    } else {
                                        currentScreen = AppScreen.LOGIN
                                    }
                                }
                            )
                        }
                        AppScreen.LOGIN -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToSignup = {
                                    currentScreen = AppScreen.SIGNUP
                                },
                                onLoginSuccess = {
                                    currentScreen = AppScreen.MAIN_CONTAINER
                                }
                            )
                        }
                        AppScreen.SIGNUP -> {
                            SignupScreen(
                                viewModel = authViewModel,
                                onNavigateBackToLogin = {
                                    currentScreen = AppScreen.LOGIN
                                },
                                onSignupSuccess = {
                                    currentScreen = AppScreen.MAIN_CONTAINER
                                }
                            )
                        }
                        AppScreen.MAIN_CONTAINER -> {
                            if (currentUser != null) {
                                MainContainer(
                                    authViewModel = authViewModel,
                                    feedViewModel = feedViewModel,
                                    currentUser = currentUser!!
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


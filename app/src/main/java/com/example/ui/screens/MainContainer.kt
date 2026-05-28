package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserAccount
import com.example.ui.theme.BenzolBlue
import com.example.ui.theme.CarbonGrey
import com.example.ui.theme.PrestigeDark
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.FeedViewModel

// Tabs enumeration for clear modularity
enum class NavigationTab {
    FEED, CREATE, PROFILE
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel,
    currentUser: UserAccount
) {
    var activeTab by remember { mutableStateOf(NavigationTab.FEED) }

    Scaffold(
        bottomBar = {
            // High class custom styled Navigation Bar
            NavigationBar(
                containerColor = PrestigeDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                // Home Feed Tab
                NavigationBarItem(
                    selected = activeTab == NavigationTab.FEED,
                    onClick = { activeTab = NavigationTab.FEED },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavigationTab.FEED) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Feed tab icon"
                        )
                    },
                    label = { Text("Feed", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BenzolBlue,
                        selectedTextColor = BenzolBlue,
                        indicatorColor = CarbonGrey,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_feed_tab")
                )

                // Create Post Tab
                NavigationBarItem(
                    selected = activeTab == NavigationTab.CREATE,
                    onClick = { activeTab = NavigationTab.CREATE },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavigationTab.CREATE) Icons.Default.AddCircle else Icons.Outlined.AddCircleOutline,
                            contentDescription = "Create post icon"
                        )
                    },
                    label = { Text("Ignite", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BenzolBlue,
                        selectedTextColor = BenzolBlue,
                        indicatorColor = CarbonGrey,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_create_tab")
                )

                // Active Profile Tab
                NavigationBarItem(
                    selected = activeTab == NavigationTab.PROFILE,
                    onClick = { activeTab = NavigationTab.PROFILE },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == NavigationTab.PROFILE) Icons.Default.AccountCircle else Icons.Outlined.AccountCircle,
                            contentDescription = "Profile icon"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BenzolBlue,
                        selectedTextColor = BenzolBlue,
                        indicatorColor = CarbonGrey,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        }
    ) { innerPadding ->
        // Animated transition layout switcher 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() with fadeOut()
                },
                label = "ScreenTransitions"
            ) { tab ->
                when (tab) {
                    NavigationTab.FEED -> {
                        HomeScreen(
                            feedViewModel = feedViewModel,
                            currentUser = currentUser
                        )
                    }
                    NavigationTab.CREATE -> {
                        CreatePostScreen(
                            feedViewModel = feedViewModel,
                            currentUser = currentUser,
                            onPublishSuccess = {
                                activeTab = NavigationTab.FEED
                            }
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            authViewModel = authViewModel,
                            feedViewModel = feedViewModel,
                            currentUser = currentUser
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.database.UserAccount
import com.example.ui.theme.AlNoorGold
import com.example.ui.theme.BenzolBlue
import com.example.ui.theme.CarbonGrey
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PrestigeDark
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.FeedViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel,
    currentUser: UserAccount
) {
    val feedState by feedViewModel.postsFlow.collectAsState()

    // Filter posts published by active user
    val userOwnPosts = remember(feedState, currentUser) {
        feedState.filter { it.post.authorEmail == currentUser.email }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Upper Profile Card Info Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Active avatar
                AsyncImage(
                    model = currentUser.avatarUrl,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, AlNoorGold, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentUser.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhite
                )

                Text(
                    text = "@${currentUser.username}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BenzolBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                val bioJoined = if (currentUser.bio.trim().isEmpty()) {
                    "Stepping inside the sphere of Benzol Al Noor lights."
                } else currentUser.bio
                Text(
                    text = bioJoined,
                    fontSize = 13.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // User Metrics Stats Row (Seeded elegantly)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrestigeDark)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricWidget(num = userOwnPosts.size.toString(), label = "Posts")
                    MetricWidget(num = "12.8K", label = "Followers")
                    MetricWidget(num = "342", label = "Following")
                    MetricWidget(num = "850", label = "Benzol Energy")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logout Button
                Button(
                    onClick = { authViewModel.logout() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CarbonGrey,
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("logout_button")
                ) {
                    Text(
                        text = "DISCONNECT ENGINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Left display title
                Text(
                    text = "YOUR LIGHTS (POSTS)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = AlNoorGold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Start
                )
            }
        }

        // List own user posts
        if (userOwnPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You haven't posted any sparks yet.\nClick on the middle tab to create your first post!",
                        fontSize = 12.sp,
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(userOwnPosts, key = { it.post.id }) { postState ->
                PostCard(
                    postState = postState,
                    onLikeToggled = { feedViewModel.toggleLike(postState.post.id) },
                    onCommentsClicked = { /* Already handles modal, profile can display inline comments if desired or standard clicks */ }
                )
            }
        }
    }
}

@Composable
fun MetricWidget(num: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = num,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = PureWhite
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MutedText
        )
    }
}

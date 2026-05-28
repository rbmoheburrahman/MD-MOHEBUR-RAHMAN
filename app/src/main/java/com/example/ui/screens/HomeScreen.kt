package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.database.Comment
import com.example.data.database.Post
import com.example.data.database.UserAccount
import com.example.ui.theme.AlNoorGold
import com.example.ui.theme.BenzolBlue
import com.example.ui.theme.CarbonGrey
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PrestigeDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftEmerald
import com.example.ui.viewmodel.FeedViewModel
import com.example.ui.viewmodel.PostWithLikeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    feedViewModel: FeedViewModel,
    currentUser: UserAccount
) {
    val feedState by feedViewModel.postsFlow.collectAsState()
    
    // Bottom Sheet State for Comment Panel
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Elegant Luxury App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo flame icon drawing
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(BenzolBlue, AlNoorGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Benzol Al Noor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "premium social circle",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                            color = AlNoorGold
                        )
                    }
                }

                // Active User Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(CarbonGrey)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentUser.avatarUrl,
                        contentDescription = "Self Avatar",
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentUser.displayName.take(8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }

            // Divider Line with visual premium glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, CarbonGrey, Color.Transparent)
                        )
                    )
            )

            // Dynamic Posts Feed containing seed & user posts
            if (feedState.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BenzolBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Fueling feed contents...", color = MutedText, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
                ) {
                    items(feedState, key = { it.post.id }) { postState ->
                        PostCard(
                            postState = postState,
                            onLikeToggled = {
                                feedViewModel.toggleLike(postState.post.id)
                            },
                            onCommentsClicked = {
                                selectedPostForComments = postState.post
                            }
                        )
                    }
                }
            }
        }

        // Expanded Comments panel using standard M3 bottom sheet
        if (selectedPostForComments != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedPostForComments = null },
                sheetState = sheetState,
                containerColor = PrestigeDark,
                contentColor = PureWhite,
                scrimColor = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                CommentSheetContent(
                    post = selectedPostForComments!!,
                    feedViewModel = feedViewModel,
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            selectedPostForComments = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PostCard(
    postState: PostWithLikeState,
    onLikeToggled: () -> Unit,
    onCommentsClicked: () -> Unit
) {
    val post = postState.post
    
    // Premium Like dynamic scales representation
    var isLikeClicked by remember { mutableStateOf(false) }
    val scaleFactor by animateFloatAsState(
        targetValue = if (isLikeClicked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f),
        label = "LikeAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CarbonGrey, RoundedCornerShape(16.dp))
            .background(PrestigeDark)
    ) {
        // Author profile top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.authorAvatar,
                contentDescription = "Author Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, AlNoorGold.copy(alpha = 0.4f), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.authorName,
                    color = PureWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.authorEmail,
                    color = MutedText,
                    fontSize = 11.sp,
                )
            }
            // Smart Formatted Timestamp
            val dateFormated = remember(post.timestamp) {
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                sdf.format(Date(post.timestamp))
            }
            Text(
                text = dateFormated,
                color = MutedText.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }

        // Post text content
        Text(
            text = post.content,
            color = PureWhite,
            fontSize = 13.5.sp,
            lineHeight = 19.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Post optional Media Image view Loaded via Coil asynchronously
        if (post.imageUrl != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CarbonGrey)
            ) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Attached Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Interactive Footer Row (Like with anims, Comments count)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heart Container click
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true)
                    ) {
                        isLikeClicked = true
                        onLikeToggled()
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("like_button_${post.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (postState.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Status Icon",
                    tint = if (postState.isLiked) LuxuryRed else MutedText,
                    modifier = Modifier
                        .size(20.dp)
                        .scale(scaleFactor)
                )

                // Trigger click release animations
                LaunchedEffect(isLikeClicked) {
                    if (isLikeClicked) {
                        kotlinx.coroutines.delay(180)
                        isLikeClicked = false
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = post.likesCount.toString(),
                    color = if (postState.isLiked) LuxuryRed else PureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Comments Navigation Trigger
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { onCommentsClicked() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("comments_button_${post.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comments Button",
                    tint = BenzolBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = post.commentsCount.toString(),
                    color = PureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentSheetContent(
    post: Post,
    feedViewModel: FeedViewModel,
    onDismiss: () -> Unit
) {
    val comments by feedViewModel.getComments(post.id).collectAsState(initial = emptyList())
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrestigeDark)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // Comment title header area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "COMMENTS (${comments.size})",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp,
                color = PureWhite,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Close",
                color = MutedText,
                fontSize = 13.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }

        // Inner Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CarbonGrey)
        )

        // List of Comments
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet.\nBe the first to share your energy!",
                            fontSize = 12.sp,
                            color = MutedText,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                items(comments) { comment ->
                    CommentItemRow(comment)
                }
            }
        }

        // Bottom Fixed Submit text area (Aesthetic floating layout)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CarbonGrey)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Write a response...", color = MutedText, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("new_comment_text"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (newCommentText.trim().isNotEmpty()) {
                            feedViewModel.submitComment(post.id, newCommentText)
                            newCommentText = ""
                        }
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (newCommentText.trim().isNotEmpty()) {
                            feedViewModel.submitComment(post.id, newCommentText)
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier.testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send comment",
                        tint = BenzolBlue
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItemRow(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = comment.authorAvatar,
            contentDescription = "Commenter avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = comment.authorName,
                    color = PureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                // Short date representation
                val dateStr = remember(comment.timestamp) {
                    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                    sdf.format(Date(comment.timestamp))
                }
                Text(
                    text = dateStr,
                    color = MutedText.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.content,
                color = PureWhite,
                fontSize = 12.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// Ripple helper to prevent missing standard values
@Composable
fun rememberRipple(bounded: Boolean) = androidx.compose.material3.ripple(bounded = bounded)

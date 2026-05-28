package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.ui.viewmodel.FeedViewModel

@Composable
fun CreatePostScreen(
    feedViewModel: FeedViewModel,
    currentUser: UserAccount,
    onPublishSuccess: () -> Unit
) {
    var contentText by remember { mutableStateOf("") }
    var imageUrlText by remember { mutableStateOf("") }
    var isPublishing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // App header tab title
        Text(
            text = "IGNITE POST",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = PureWhite,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card text editor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CarbonGrey, RoundedCornerShape(16.dp))
                .background(PrestigeDark)
                .padding(16.dp)
        ) {
            // Logged in Author row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentUser.avatarUrl,
                    contentDescription = "Active author profile",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentUser.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "Publishing to Benzol Feed...",
                        fontSize = 10.sp,
                        color = AlNoorGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body Multiline Editor
            OutlinedTextField(
                value = contentText,
                onValueChange = { if (it.length <= 280) contentText = it },
                placeholder = {
                    Text(
                        text = "What's fueling your thoughts today? Share your light... (Max 280 chars)",
                        color = MutedText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("post_content_input")
            )

            // Content length warning counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${contentText.length}/280",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (contentText.length >= 260) AlNoorGold else MutedText.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image Attachment Field
        OutlinedTextField(
            value = imageUrlText,
            onValueChange = { imageUrlText = it },
            label = { Text("Attach Media Photo URL (Optional)") },
            placeholder = { Text("https://...") },
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BenzolBlue,
                unfocusedBorderColor = CarbonGrey,
                focusedLabelColor = BenzolBlue,
                unfocusedLabelColor = MutedText,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_image_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Premium Publish Button
        Button(
            onClick = {
                if (contentText.trim().isNotEmpty()) {
                    isPublishing = true
                    val mediaUrl = if (imageUrlText.trim().isEmpty()) null else imageUrlText.trim()
                    feedViewModel.publishPost(
                        content = contentText,
                        imageUrl = mediaUrl,
                        onComplete = {
                            isPublishing = false
                            contentText = ""
                            imageUrlText = ""
                            onPublishSuccess()
                        }
                    )
                }
            },
            enabled = contentText.trim().isNotEmpty() && !isPublishing,
            colors = ButtonDefaults.buttonColors(
                containerColor = BenzolBlue,
                contentColor = ObsidianBlack,
                disabledContainerColor = CarbonGrey,
                disabledContentColor = MutedText
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("publish_post_button")
        ) {
            if (isPublishing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = ObsidianBlack
                )
            } else {
                Text(
                    text = "IGNITE THE STAGE",
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Avoid navigation overlapping space
    }
}

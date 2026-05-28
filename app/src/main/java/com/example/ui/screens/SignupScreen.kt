package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlNoorGold
import com.example.ui.theme.BenzolBlue
import com.example.ui.theme.CarbonGrey
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PrestigeDark
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    viewModel: AuthViewModel,
    onNavigateBackToLogin: () -> Unit,
    onSignupSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authUiState.collectAsState()

    // Observe signup status
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onSignupSuccess()
            viewModel.resetState()
        } else if (authState is AuthUiState.Error) {
            errorMessage = (authState as AuthUiState.Error).message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.TopCenter
    ) {
        // Subtle Golden background light glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AlNoorGold.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Upper Back Nav Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigateBackToLogin() },
                    modifier = Modifier.testTag("back_to_login_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = PureWhite
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CREATE ACCOUNT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "✨ IGNITE YOUR LIGHT ✨",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = AlNoorGold
            )

            Text(
                text = "Join our prestigious content feed",
                fontSize = 13.sp,
                color = MutedText,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Dynamic Core Signup Form Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CarbonGrey, RoundedCornerShape(20.dp))
                    .background(PrestigeDark)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Address
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "EmailIcon", tint = MutedText)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Unique Username
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Select Username") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "UserIcon", tint = MutedText)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Display Public Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = {
                        displayName = it
                        errorMessage = null
                    },
                    label = { Text("Display Name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "FaceIcon", tint = MutedText)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio (Optional)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "BioIcon", tint = MutedText)
                    },
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Avatar Photo Link url
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Url (Optional)") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Secure Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "LockIcon", tint = MutedText)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BenzolBlue,
                        unfocusedBorderColor = CarbonGrey,
                        focusedLabelColor = BenzolBlue,
                        unfocusedLabelColor = MutedText,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error alert
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                }

                // Register Button
                Button(
                    onClick = {
                        if (email.trim().isEmpty() || username.trim().isEmpty() || displayName.trim().isEmpty() || password.trim().isEmpty()) {
                            errorMessage = "All required fields must be completed."
                        } else if (!email.contains("@") || !email.contains(".")) {
                            errorMessage = "Please enter a valid email address."
                        } else {
                            viewModel.signup(
                                email = email,
                                username = username,
                                displayName = displayName,
                                passwordPlain = password,
                                avatarUrl = avatarUrl,
                                bio = bio
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BenzolBlue,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("signup_button")
                ) {
                    if (authState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            color = ObsidianBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "START THE ENGINE",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation back to Sign in
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    color = MutedText,
                    fontSize = 13.sp
                )
                Text(
                    text = "Sign In",
                    color = BenzolBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { onNavigateBackToLogin() }
                        .padding(4.dp)
                )
            }
        }
    }
}

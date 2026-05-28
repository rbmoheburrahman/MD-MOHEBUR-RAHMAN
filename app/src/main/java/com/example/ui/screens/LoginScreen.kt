package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authUiState.collectAsState()

    // Observe login success to trigger navigation
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        } else if (authState is AuthUiState.Error) {
            errorMessage = (authState as AuthUiState.Error).message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Background Orb
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BenzolBlue.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Branding Glow Icon
            Text(
                text = "🔥",
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "BENZOL AL NOOR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = PureWhite
            )

            Text(
                text = "Access the circle of light",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = AlNoorGold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Dark Card container containing Form fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CarbonGrey, RoundedCornerShape(20.dp))
                    .background(PrestigeDark)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WELCOME BACK",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = PureWhite,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email / Username Input
                OutlinedTextField(
                    value = emailOrUsername,
                    onValueChange = {
                        emailOrUsername = it
                        errorMessage = null
                    },
                    label = { Text("Email or Username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "User icon",
                            tint = MutedText
                        )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password icon",
                            tint = MutedText
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle visibility",
                                tint = MutedText
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Display local message error if any
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

                // Submit Button
                Button(
                    onClick = {
                        if (emailOrUsername.trim().isEmpty() || password.trim().isEmpty()) {
                            errorMessage = "Please enter both credentials."
                        } else {
                            viewModel.login(emailOrUsername, password)
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
                        .testTag("login_button")
                ) {
                    if (authState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            color = ObsidianBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "IGNITE PORTAL",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Guest Quick Bypass for instantaneous login & preview testing
                TextButton(
                    onClick = {
                        // Quick log in as seeded profile
                        viewModel.login("benzol_team@noor.com", "password")
                    },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "⚡ Enter as Guest (Instant Preview)",
                        color = AlNoorGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation to Sign up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = MutedText,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign Up",
                    color = BenzolBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onNavigateToSignup() }
                        .padding(4.dp)
                )
            }
        }
    }
}



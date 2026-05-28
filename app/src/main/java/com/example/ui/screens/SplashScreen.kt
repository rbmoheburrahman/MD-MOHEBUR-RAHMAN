package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlNoorGold
import com.example.ui.theme.BenzolBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onProgressFinished: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.7f) }

    LaunchedEffect(key1 = true) {
        // Run glorious simultaneous entry animations
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
        delay(1200) // Keep the golden-neon vibe shining for a moment
        onProgressFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBlack,
                        Color(0xFF040508)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Elegant Vector Canvas Light Drawing (Benzol fuel flame + orbit gold ring)
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .padding(16.dp)
            ) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                
                // Draw Outer Golden Al Noor Glow ring
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AlNoorGold.copy(alpha = 0.4f), Color.Transparent),
                        center = centerOffset,
                        radius = size.width / 1.5f
                    ),
                    radius = size.width / 1.5f
                )

                // Draw Outer Golden Ring Stroke
                drawCircle(
                    color = AlNoorGold,
                    radius = size.width / 2.3f,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw Glowing Benzol Fuel Core (glowing neon circle with offset angle)
                drawCircle(
                    color = BenzolBlue,
                    radius = size.width / 4.5f,
                    style = Stroke(width = 5.dp.toPx())
                )

                // Central high-light core
                drawCircle(
                    color = PureWhite,
                    radius = size.width / 10f
                )

                // Golden ray lines symbolizing "Al Noor" (The Light)
                val rayCount = 8
                val radiusInner = size.width / 2.1f
                val radiusOuter = size.width / 1.8f
                for (i in 0 until rayCount) {
                    val angle = (i * 2 * Math.PI / rayCount).toFloat()
                    val startX = centerOffset.x + radiusInner * Math.cos(angle.toDouble()).toFloat()
                    val startY = centerOffset.y + radiusInner * Math.sin(angle.toDouble()).toFloat()
                    val endX = centerOffset.x + radiusOuter * Math.cos(angle.toDouble()).toFloat()
                    val endY = centerOffset.y + radiusOuter * Math.sin(angle.toDouble()).toFloat()
                    
                    drawLine(
                        color = AlNoorGold.copy(alpha = 0.8f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Premium Styled App Name
            Text(
                text = "BENZOL AL NOOR",
                fontSize = 28.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PureWhite,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "IGNITE THE PREMIUM LIGHT",
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold,
                color = BenzolBlue,
                fontFamily = FontFamily.Monospace
            )
        }

        // Mini elegant copyright label at the bottom
        Text(
            text = "Benzol Media • Established 2026",
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = MutedText.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}

package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val BenzolDarkColorScheme = darkColorScheme(
    primary = BenzolBlue,
    secondary = AlNoorGold,
    tertiary = SoftEmerald,
    background = ObsidianBlack,
    surface = PrestigeDark,
    onPrimary = ObsidianBlack,
    onSecondary = ObsidianBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = CarbonGrey,
    onSurfaceVariant = MutedText,
    outline = CarbonGrey
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark exclusive mode for full Premium experience
    dynamicColor: Boolean = false, // Disable dynamic colors to keep Benzol Al Noor elite branding pristine
    content: @Composable () -> Unit,
) {
    // We enforce our Benzol Al Noor branding palette to achieve the "glowing light" and "neon gasoline blue" experience.
    val colorScheme = BenzolDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

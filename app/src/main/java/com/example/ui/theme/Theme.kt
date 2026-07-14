package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = HelixBlue,
    secondary = HelixDarkGray,
    tertiary = HelixGreen,
    background = HelixDarkNavy,
    surface = HelixDarkGray,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HelixBlue,
    secondary = HelixDarkGray,
    tertiary = HelixGreen,
    background = HelixLightGray,
    surface = HelixCardBackground,
    onPrimary = Color.White,
    onBackground = HelixDarkNavy,
    onSurface = HelixDarkGray,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force a clean white-focused medical theme as requested
  dynamicColor: Boolean = false, // Disable dynamic colors for custom branded UI
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

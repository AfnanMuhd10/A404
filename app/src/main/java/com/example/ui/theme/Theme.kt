package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BentoLightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    onPrimary = Color.White,
    primaryContainer = BentoPrimaryContainer,
    onPrimaryContainer = BentoOnPrimaryContainer,
    
    secondary = BentoCyan,
    onSecondary = Color.White,
    secondaryContainer = BentoCyanContainer,
    onSecondaryContainer = BentoOnCyanContainer,
    
    tertiary = BentoEmerald,
    onTertiary = Color.White,
    tertiaryContainer = BentoEmeraldContainer,
    onTertiaryContainer = Color(0xFF00210B),
    
    background = BentoBackground,
    onBackground = BentoTextPrimary,
    
    surface = BentoSurface,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoSurfaceVariant,
    onSurfaceVariant = BentoTextSecondary,
    
    outline = BentoBorder,
    outlineVariant = BentoBorderLight
)

private val BentoDarkColorScheme = darkColorScheme(
    primary = BentoPrimaryContainer,
    onPrimary = BentoOnPrimaryContainer,
    primaryContainer = BentoPrimary,
    onPrimaryContainer = Color.White,
    
    secondary = Color(0xFF80DEEA),
    onSecondary = Color(0xFF00363F),
    secondaryContainer = BentoCyanDark,
    onSecondaryContainer = BentoCyanContainer,
    
    tertiary = Color(0xFFA5D6A7),
    onTertiary = Color(0xFF003915),
    
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E5),
    
    surface = Color(0xFF1D1B20),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2830),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    outline = Color(0xFF49454F),
    outlineVariant = Color(0xFF322F37)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to Bento Grid light theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BentoDarkColorScheme else BentoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


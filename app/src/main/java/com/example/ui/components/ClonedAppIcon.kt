package com.example.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppCategory
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoRose
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoTextPrimary

@Composable
fun ClonedAppIcon(
    packageName: String,
    appName: String,
    category: AppCategory = AppCategory.UTILITIES,
    size: Dp = 56.dp,
    badgeText: String? = "2",
    badgeType: String = "NUMBER",
    tintHex: String = "#06B6D4",
    shape: String = "SQUIRCLE",
    rotation: Float = 0f,
    flipHorizontal: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parsedColor = remember(tintHex) {
        try {
            Color(android.graphics.Color.parseColor(tintHex))
        } catch (e: Exception) {
            BentoPrimary
        }
    }

    val iconDrawable: Drawable? = remember(packageName) {
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    val cornerShape = remember(shape) {
        when (shape.uppercase()) {
            "CIRCLE" -> CircleShape
            "ROUNDED" -> RoundedCornerShape(10.dp)
            "HEXAGON" -> RoundedCornerShape(20.dp)
            else -> RoundedCornerShape(16.dp) // SQUIRCLE
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Base Icon Box
        Box(
            modifier = Modifier
                .size(size)
                .rotate(rotation)
                .scale(scaleX = if (flipHorizontal) -1f else 1f, scaleY = 1f)
                .clip(cornerShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            parsedColor.copy(alpha = 0.18f),
                            parsedColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = parsedColor.copy(alpha = 0.5f),
                    shape = cornerShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (iconDrawable != null) {
                val bitmap = remember(iconDrawable) {
                    try {
                        iconDrawable.toBitmap(
                            width = 96,
                            height = 96,
                            config = android.graphics.Bitmap.Config.ARGB_8888
                        ).asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap,
                        contentDescription = appName,
                        modifier = Modifier
                            .size(size * 0.72f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    FallbackAppMonogram(appName = appName, category = category, color = parsedColor)
                }
            } else {
                FallbackAppMonogram(appName = appName, category = category, color = parsedColor)
            }
        }

        // Clone Badge Overlay
        if (badgeType != "NONE" && !badgeText.isNullOrBlank()) {
            CloneBadge(
                badgeText = badgeText,
                badgeType = badgeType,
                tintColor = parsedColor,
                parentSize = size,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun FallbackAppMonogram(
    appName: String,
    category: AppCategory,
    color: Color
) {
    val initial = appName.firstOrNull()?.uppercase() ?: "A"
    val icon = when (category) {
        AppCategory.SOCIAL -> Icons.Default.Chat
        AppCategory.GAMES -> Icons.Default.Games
        AppCategory.PRODUCTIVITY -> Icons.Default.Work
        AppCategory.MEDIA -> Icons.Default.MusicNote
        else -> null
    }

    if (icon != null && appName.length > 8) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
    } else {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = color
            )
        )
    }
}

@Composable
fun CloneBadge(
    badgeText: String,
    badgeType: String,
    tintColor: Color,
    parentSize: Dp,
    modifier: Modifier = Modifier
) {
    val badgeSize = (parentSize.value * 0.42f).coerceIn(18f, 26f).dp
    val badgeBg = when (badgeType.uppercase()) {
        "SHIELD" -> BentoIndigo
        "DOT" -> BentoEmerald
        "TEXT" -> tintColor
        else -> BentoPrimary
    }

    Box(
        modifier = modifier
            .offset(x = 3.dp, y = 3.dp)
            .size(badgeSize)
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .border(1.dp, BentoBorder, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (badgeText == "🔒" || badgeType.equals("SHIELD", ignoreCase = true)) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Secured Clone",
                tint = Color.White,
                modifier = Modifier.size(badgeSize * 0.65f)
            )
        } else {
            Text(
                text = badgeText.take(4),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (badgeSize.value * 0.45f).sp,
                    color = Color.White
                )
            )
        }
    }
}


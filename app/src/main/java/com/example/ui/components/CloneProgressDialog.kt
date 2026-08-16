package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoCyanLight
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldContainer
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import kotlinx.coroutines.delay

@Composable
fun CloneProgressDialog(
    appName: String = "App",
    cloneName: String = "Cloned Instance",
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var rawProgress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "cloning_progress"
    )

    val steps = remember {
        listOf(
            "Analyzing APK manifest & package structure..." to Icons.Default.DataObject,
            "Refactoring namespace & signature keys..." to Icons.Default.Key,
            "Allocating isolated virtual storage sandbox..." to Icons.Default.FolderZip,
            "Injecting spoofed device identity hooks..." to Icons.Default.Fingerprint,
            "Generating sandboxed runtime container..." to Icons.Default.Layers,
            "Clone ready! Verified isolated sandbox." to Icons.Default.CheckCircle
        )
    }

    LaunchedEffect(Unit) {
        delay(400)
        stepIndex = 1
        rawProgress = 0.30f
        delay(550)
        stepIndex = 2
        rawProgress = 0.55f
        delay(600)
        stepIndex = 3
        rawProgress = 0.78f
        delay(500)
        stepIndex = 4
        rawProgress = 0.92f
        delay(450)
        stepIndex = 5
        rawProgress = 1.0f
        delay(400)
    }

    val isFinished = stepIndex >= steps.size - 1

    Dialog(
        onDismissRequest = { if (isFinished) onComplete() },
        properties = DialogProperties(dismissOnBackPress = isFinished, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                .testTag("clone_progress_dialog"),
            colors = CardDefaults.cardColors(containerColor = BentoSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFinished) BentoEmeraldContainer else BentoPrimaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFinished) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = BentoEmerald,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = BentoPrimary,
                            strokeWidth = 3.5.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (isFinished) "Clone Container Ready!" else "Creating Virtual Clone...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                )

                Text(
                    text = "$appName → $cloneName",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BentoPrimary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isFinished) BentoEmerald else BentoPrimary,
                    trackColor = BentoSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SANDBOX ENGINE v2.6",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Active Step Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoSurfaceVariant)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = steps[stepIndex.coerceIn(0, steps.size - 1)].second,
                            contentDescription = null,
                            tint = if (isFinished) BentoEmerald else BentoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = steps[stepIndex.coerceIn(0, steps.size - 1)].first,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextPrimary,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                if (isFinished) {
                    Spacer(modifier = Modifier.height(22.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("clone_complete_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Launch & Open Clone",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}


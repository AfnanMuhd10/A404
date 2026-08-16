package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClonedApp
import com.example.ui.components.ClonedAppIcon
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoAmberContainer
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoCyanLight
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldContainer
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoIndigoContainer
import com.example.ui.theme.BentoIndigoLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoRose
import com.example.ui.theme.BentoRoseContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.utils.CloneGenerator
import com.example.viewmodel.AppClonerViewModel
import com.example.viewmodel.ScreenState
import kotlinx.coroutines.delay

data class SimulatedChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val time: String,
    val isMe: Boolean
)

@Composable
fun VirtualSandboxInstanceScreen(
    viewModel: AppClonerViewModel,
    clone: ClonedApp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var sessionSeconds by remember { mutableIntStateOf(1) }
    var activeAccountTab by remember { mutableIntStateOf(1) } // 0: Primary, 1: Cloned #2
    var inputMessage by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            SimulatedChatMessage("1", "Alex (Work)", "Hey! Is this the separate work profile?", "10:14 AM", false),
            SimulatedChatMessage("2", "Me", "Yes, running in isolated sandbox container #2!", "10:15 AM", true),
            SimulatedChatMessage("3", "Alex (Work)", "Awesome, cookies and storage are completely separated.", "10:16 AM", false)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            sessionSeconds++
        }
    }

    val minutes = sessionSeconds / 60
    val seconds = sessionSeconds % 60
    val formattedDuration = "%02d:%02d".format(minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Container Status Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoSurface)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, BentoBorder))
                    .padding(top = 14.dp, start = 12.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenState.DASHBOARD) },
                            modifier = Modifier.testTag("btn_back_from_runner")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to dashboard",
                                tint = BentoTextPrimary
                            )
                        }

                        ClonedAppIcon(
                            packageName = clone.originalPackageName,
                            appName = clone.cloneName,
                            size = 42.dp,
                            badgeText = clone.iconBadgeText,
                            badgeType = clone.iconBadgeType,
                            tintHex = clone.iconTintHex,
                            shape = clone.iconShape,
                            rotation = clone.iconRotation,
                            flipHorizontal = clone.iconFlipHorizontal
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = clone.cloneName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BentoEmerald)
                                )
                            }

                            Text(
                                text = "Session: $formattedDuration • Virtual Space Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoEmerald,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    // Stop Button
                    Button(
                        onClick = { viewModel.stopCloneInstance(clone) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoRoseContainer,
                            contentColor = BentoRose
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stop", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Virtual Telemetry Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(clone.fakeModelName, style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 10.sp))
                        }

                        if (clone.spoofLocation) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(clone.spoofLocationName, style = MaterialTheme.typography.labelSmall.copy(color = BentoIndigoLight, fontSize = 10.sp))
                            }
                        }
                    }

                    Text(
                        text = "SANDBOX ENCRYPTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Dual Account Switcher Tabs
            TabRow(
                selectedTabIndex = activeAccountTab,
                containerColor = BentoSurface,
                contentColor = BentoPrimary,
                divider = {}
            ) {
                Tab(
                    selected = activeAccountTab == 0,
                    onClick = { activeAccountTab = 0 },
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Account #1 (Main OS Space)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (activeAccountTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeAccountTab == 0) BentoTextPrimary else BentoTextSecondary
                        )
                    )
                }

                Tab(
                    selected = activeAccountTab == 1,
                    onClick = { activeAccountTab = 1 },
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Account #2 (Cloned Sandbox)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (activeAccountTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeAccountTab == 1) BentoPrimary else BentoTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BentoPrimary)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "ALT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
            }

            // Sandbox Content Area
            if (activeAccountTab == 0) {
                // Primary Account Placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwitchAccount,
                                contentDescription = null,
                                tint = BentoPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Primary Account Profile",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "This is your main host app workspace. Switch back to Account #2 to test sandboxed dual account isolation.",
                                style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    try {
                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(clone.originalPackageName)
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    } catch (e: Exception) {
                                        // Ignore if package not installed natively
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant, contentColor = BentoTextPrimary),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                            ) {
                                Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Native Host App")
                            }
                        }
                    }
                }
            } else {
                // Active Cloned Sandboxed Workspace
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Chat / Interactive Feed container
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (msg.isMe) BentoPrimaryContainer else BentoSurface
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (msg.isMe) BentoPrimary.copy(alpha = 0.3f) else BentoBorder
                                    ),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        if (!msg.isMe) {
                                            Text(
                                                text = msg.sender,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = BentoPrimary
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                        }
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = BentoTextPrimary)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.time,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = BentoTextMuted,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Input Row for simulated isolated message
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = { Text("Send sandboxed message...", color = BentoTextMuted, fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BentoSurface,
                                unfocusedContainerColor = BentoSurface,
                                focusedBorderColor = BentoPrimary,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoTextPrimary,
                                unfocusedTextColor = BentoTextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    messages.add(
                                        SimulatedChatMessage(
                                            id = System.currentTimeMillis().toString(),
                                            sender = "Me",
                                            text = inputMessage,
                                            time = "Now",
                                            isMe = true
                                        )
                                    )
                                    inputMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BentoPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Quick Sandbox Control Dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoSurface)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, BentoBorder))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rotate Identity
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.rotateIdentityForClone(clone) }
                            .padding(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Cached, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Rotate ID", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 10.sp))
                    }

                    // Flush Cache
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.clearCloneSandbox(clone) }
                            .padding(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = BentoAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Flush Cache", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 10.sp))
                    }

                    // Sandbox Files
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.openSandboxExplorer(clone) }
                            .padding(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, tint = BentoIndigo, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Storage", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 10.sp))
                    }

                    // Security Settings
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.navigateTo(ScreenState.PRIVACY_VAULT) }
                            .padding(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = BentoEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Vault", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 10.sp))
                    }
                }
            }
        }
    }
}


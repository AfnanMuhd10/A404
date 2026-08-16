package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClonedApp
import com.example.ui.components.ClonedAppIcon
import com.example.ui.components.PinLockDialog
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoAmberContainer
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoCyanContainer
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
import com.example.ui.theme.BentoSurfaceAlt
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.utils.CloneGenerator
import com.example.viewmodel.AppClonerViewModel
import com.example.viewmodel.ScreenState

@Composable
fun ClonesDashboardScreen(
    viewModel: AppClonerViewModel,
    clonedApps: List<ClonedApp>,
    modifier: Modifier = Modifier
) {
    var selectedDashboardTab by remember { mutableIntStateOf(0) }
    var pendingLockedClone by remember { mutableStateOf<ClonedApp?>(null) }
    var showQuickBoostSnackbar by remember { mutableStateOf(false) }

    val runningClonesCount = clonedApps.count { it.isRunning }
    val totalSandboxStorage = clonedApps.sumOf { it.sandboxStorageBytes }

    Box(modifier = modifier.fillMaxSize().background(BentoBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Top App Bar
            item {
                DashboardHeader(
                    runningCount = runningClonesCount,
                    onOpenDisguise = { viewModel.setFakeCalculatorDisguise(true) },
                    onOpenVault = { viewModel.navigateTo(ScreenState.PRIVACY_VAULT) },
                    onOpenLogs = { viewModel.navigateTo(ScreenState.LOGS_VIEWER) }
                )
            }

            // Bento Grid Metrics Section
            item {
                BentoMetricsGrid(
                    totalClones = clonedApps.size,
                    runningCount = runningClonesCount,
                    totalStorageBytes = totalSandboxStorage,
                    onBoostMemory = {
                        viewModel.stopAllInstances()
                        showQuickBoostSnackbar = true
                    }
                )
            }

            // Dashboard Filter Tabs
            item {
                DashboardTabBar(
                    selectedTabIndex = selectedDashboardTab,
                    onTabSelected = { selectedDashboardTab = it },
                    totalClones = clonedApps.size,
                    runningClones = runningClonesCount
                )
            }

            // Filtered Clones List
            val displayedClones = when (selectedDashboardTab) {
                1 -> clonedApps.filter { it.isRunning }
                2 -> clonedApps.filter { it.isIncognito || it.pinProtection != null }
                else -> clonedApps
            }

            if (displayedClones.isEmpty()) {
                item {
                    EmptyClonesState(
                        tabIndex = selectedDashboardTab,
                        onAddClone = { viewModel.navigateTo(ScreenState.SELECT_APP) }
                    )
                }
            } else {
                items(displayedClones, key = { it.id }) { clone ->
                    CloneAppCard(
                        clone = clone,
                        onLaunch = {
                            if (!clone.pinProtection.isNullOrBlank()) {
                                pendingLockedClone = clone
                            } else {
                                viewModel.launchCloneInstance(clone)
                            }
                        },
                        onStop = { viewModel.stopCloneInstance(clone) },
                        onOpenFiles = { viewModel.openSandboxExplorer(clone) },
                        onRotateId = { viewModel.rotateIdentityForClone(clone) },
                        onClearData = { viewModel.clearCloneSandbox(clone) },
                        onDelete = { viewModel.deleteClone(clone) }
                    )
                }
            }
        }

        // Floating Action Button to Add Clone
        FloatingActionButton(
            onClick = { viewModel.navigateTo(ScreenState.SELECT_APP) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_add_clone"),
            containerColor = BentoPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Clone App",
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clone App",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }

        // PIN Verification Dialog if clone is locked
        pendingLockedClone?.let { lockedClone ->
            PinLockDialog(
                targetName = lockedClone.cloneName,
                expectedPin = lockedClone.pinProtection ?: "",
                onSuccess = {
                    val cloneToLaunch = lockedClone
                    pendingLockedClone = null
                    viewModel.launchCloneInstance(cloneToLaunch)
                },
                onDismiss = {
                    pendingLockedClone = null
                }
            )
        }
    }
}

@Composable
fun DashboardHeader(
    runningCount: Int,
    onOpenDisguise: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenLogs: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(BentoPrimary, BentoCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "App Cloner Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "App Cloner",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = BentoTextPrimary,
                                fontSize = 22.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BentoPrimaryContainer)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BENTO PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = if (runningCount > 0) "$runningCount sandboxes active" else "Isolated dual-app containers",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (runningCount > 0) BentoEmerald else BentoTextSecondary,
                            fontWeight = if (runningCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }

            // Action Icons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onOpenDisguise,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoSurface)
                        .border(1.dp, BentoBorder, CircleShape)
                        .testTag("btn_calculator_disguise")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Secret Calculator Disguise",
                        tint = BentoAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenVault,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoSurface)
                        .border(1.dp, BentoBorder, CircleShape)
                        .testTag("btn_privacy_vault")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Vault",
                        tint = BentoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenLogs,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoSurface)
                        .border(1.dp, BentoBorder, CircleShape)
                        .testTag("btn_activity_logs")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Activity Logs",
                        tint = BentoTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BentoMetricsGrid(
    totalClones: Int,
    runningCount: Int,
    totalStorageBytes: Long,
    onBoostMemory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Bento Row 1: Cloned Spaces & Sandbox Data
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Bento Tile 1: Cloned Spaces
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLONED SPACES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = BentoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$totalClones",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoTextPrimary,
                            fontSize = 28.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (runningCount > 0) BentoEmeraldContainer else BentoSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (runningCount > 0) "$runningCount ACTIVE" else "0 ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (runningCount > 0) BentoEmerald else BentoTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Bento Tile 2: Sandbox Data
            Card(
                modifier = Modifier
                    .weight(1.0f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SANDBOX DATA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoIndigoContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = BentoIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = CloneGenerator.formatBytes(totalStorageBytes),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoTextPrimary,
                            fontSize = 22.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Isolated Storage",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Bento Row 2: Device Hooks & Speed Booster
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Bento Tile 3: Device Hooks
            Card(
                modifier = Modifier
                    .weight(1.0f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DEVICE HOOKS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BentoEmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = BentoEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PROTECTED",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BentoEmerald,
                            fontSize = 15.sp
                        )
                    )

                    Text(
                        text = "IMEI & MAC Masked",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Bento Tile 4: RAM Optimizer
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
                    .clickable { onBoostMemory() }
                    .testTag("btn_boost_memory"),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OPTIMIZER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BentoCyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = BentoCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Purge RAM",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary,
                                fontSize = 14.sp
                            )
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BentoCyanContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PURGE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BentoCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "Release memory handles",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardTabBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    totalClones: Int,
    runningClones: Int
) {
    val tabs = listOf(
        "All Clones ($totalClones)",
        "Active ($runningClones)",
        "Protected Vault"
    )

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        containerColor = Color.Transparent,
        contentColor = BentoPrimary,
        edgePadding = 0.dp,
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) BentoPrimary else BentoSurface)
                    .border(
                        1.dp,
                        if (isSelected) BentoPrimary else BentoBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else BentoTextSecondary
                    )
                )
            }
        }
    }
}

@Composable
fun CloneAppCard(
    clone: ClonedApp,
    onLaunch: () -> Unit,
    onStop: () -> Unit,
    onOpenFiles: () -> Unit,
    onRotateId: () -> Unit,
    onClearData: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.5.dp,
                if (clone.isRunning) BentoEmerald.copy(alpha = 0.7f) else BentoBorder,
                RoundedCornerShape(22.dp)
            )
            .testTag("clone_card_${clone.id}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customized App Icon
                ClonedAppIcon(
                    packageName = clone.originalPackageName,
                    appName = clone.cloneName,
                    size = 54.dp,
                    badgeText = clone.iconBadgeText,
                    badgeType = clone.iconBadgeType,
                    tintHex = clone.iconTintHex,
                    shape = clone.iconShape,
                    rotation = clone.iconRotation,
                    flipHorizontal = clone.iconFlipHorizontal
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Title and Package Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = clone.cloneName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!clone.pinProtection.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BentoRoseContainer)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "PIN Protected",
                                    tint = BentoRose,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        if (clone.spoofLocation) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BentoIndigoContainer)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "GPS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BentoIndigo,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = clone.clonePackageName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Running Pill
                        if (clone.isRunning) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BentoEmeraldContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(BentoEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Running",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BentoEmerald,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Size: ${CloneGenerator.formatBytes(clone.sandboxStorageBytes)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontSize = 11.sp
                            )
                        )

                        Text(
                            text = "• Launches: ${clone.launchCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Launch / Stop Primary Action Button
                if (clone.isRunning) {
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoRoseContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Instance",
                            tint = BentoRose,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onLaunch,
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("launch_btn_${clone.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Open",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Virtual Identity Info Chip Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BentoSurfaceVariant)
                    .border(1.dp, BentoBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = BentoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ID: ${clone.fakeAndroidId.take(8).uppercase()}...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Text(
                        text = clone.fakeModelName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sandbox Management Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Sandbox storage button
                    OutlinedButton(
                        onClick = onOpenFiles,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = BentoIndigo,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Storage",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Rotate Fingerprint
                    OutlinedButton(
                        onClick = onRotateId,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = null,
                            tint = BentoCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rotate ID",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    // Clear Sandbox cache
                    OutlinedButton(
                        onClick = onClearData,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = BentoAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Flush Cache",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Delete Clone Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Clone",
                        tint = BentoTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyClonesState(
    tabIndex: Int,
    onAddClone: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = BentoSurface)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(BentoPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (tabIndex == 1) "No Active Sandboxes" else "No Cloned Apps Yet",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Clone WhatsApp, Instagram, Telegram, or any installed app to run multiple isolated accounts with custom identities.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BentoTextSecondary,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onAddClone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clone an App Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


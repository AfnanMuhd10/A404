package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ApkFileType
import com.example.model.ApkInternalFile
import com.example.model.ApkPackageInfo
import com.example.model.ApkPermissionItem
import com.example.model.ApkSourceType
import com.example.model.ApkStringResource
import com.example.ui.components.ClonedAppIcon
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoAmberContainer
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCyan
import com.example.ui.theme.BentoCyanContainer
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldContainer
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.BentoIndigoContainer
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
import com.example.utils.ApkParserHelper
import com.example.utils.CloneGenerator
import com.example.viewmodel.AppClonerViewModel
import com.example.viewmodel.ScreenState

@Composable
fun ApkEditorScreen(
    viewModel: AppClonerViewModel,
    apkInfo: ApkPackageInfo,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showBuildDialog by remember { mutableStateOf(false) }
    var showBuiltSuccessDialog by remember { mutableStateOf(false) }
    var activeEditingFile by remember { mutableStateOf<ApkInternalFile?>(null) }
    var activeRenameFile by remember { mutableStateOf<ApkInternalFile?>(null) }
    var showAddFileDialog by remember { mutableStateOf(false) }
    var showAddPermissionDialog by remember { mutableStateOf(false) }
    var showAddStringDialog by remember { mutableStateOf(false) }

    val isBuilding by viewModel.isApkBuilding.collectAsState()
    val buildProgress by viewModel.apkBuildProgress.collectAsState()
    val buildStep by viewModel.apkBuildStep.collectAsState()
    val buildLogs by viewModel.apkBuildLogs.collectAsState()
    val builtResult by viewModel.builtApkResult.collectAsState()

    val tabTitles = listOf("Properties", "Manifest & Perms", "Strings (i18n)", "Files & Assets", "Icon Studio")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            ApkEditorHeader(
                apkInfo = apkInfo,
                onBack = { viewModel.navigateTo(ScreenState.SELECT_APP) },
                onRebuildApk = { showBuildDialog = true },
                onCloneDirectly = { viewModel.cloneDirectlyFromApk(apkInfo) }
            )

            // Sub Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BentoSurface,
                contentColor = BentoPrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = BentoBorder, thickness = 1.dp) }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) BentoPrimary else BentoTextSecondary
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Tune
                                    1 -> Icons.Default.Security
                                    2 -> Icons.Default.TextFields
                                    3 -> Icons.Default.FolderZip
                                    else -> Icons.Default.Palette
                                },
                                contentDescription = title,
                                modifier = Modifier.size(18.dp),
                                tint = if (selectedTabIndex == index) BentoPrimary else BentoTextMuted
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTabIndex) {
                    0 -> ApkPropertiesTab(apkInfo = apkInfo, viewModel = viewModel)
                    1 -> ApkPermissionsTab(
                        apkInfo = apkInfo,
                        viewModel = viewModel,
                        onAddPermission = { showAddPermissionDialog = true }
                    )
                    2 -> ApkStringsTab(
                        apkInfo = apkInfo,
                        viewModel = viewModel,
                        onAddString = { showAddStringDialog = true }
                    )
                    3 -> ApkFilesTab(
                        apkInfo = apkInfo,
                        viewModel = viewModel,
                        onOpenFile = { activeEditingFile = it },
                        onRenameFile = { activeRenameFile = it },
                        onAddFile = { showAddFileDialog = true },
                        onDeleteFile = { viewModel.deleteApkFile(it.id) }
                    )
                    4 -> ApkIconStudioTab(apkInfo = apkInfo, viewModel = viewModel)
                }
            }
        }

        // Modal for editing text/xml files
        activeEditingFile?.let { file ->
            ApkFileContentEditorDialog(
                file = file,
                onDismiss = { activeEditingFile = null },
                onSave = { newContent ->
                    viewModel.updateApkFileContent(file.id, newContent)
                    activeEditingFile = null
                }
            )
        }

        // Modal for renaming internal files
        activeRenameFile?.let { file ->
            ApkRenameFileDialog(
                file = file,
                onDismiss = { activeRenameFile = null },
                onRename = { newName ->
                    viewModel.renameApkFile(file.id, newName)
                    activeRenameFile = null
                }
            )
        }

        // Modal for adding a new internal file
        if (showAddFileDialog) {
            ApkAddFileDialog(
                onDismiss = { showAddFileDialog = false },
                onAdd = { relPath, name, content, type ->
                    viewModel.addApkFile(relPath, name, content, type)
                    showAddFileDialog = false
                }
            )
        }

        // Modal for adding a permission
        if (showAddPermissionDialog) {
            ApkAddPermissionDialog(
                currentPermissions = apkInfo.permissions,
                onDismiss = { showAddPermissionDialog = false },
                onAdd = { name, desc, isDanger ->
                    viewModel.addApkPermission(name, desc, isDanger)
                    showAddPermissionDialog = false
                }
            )
        }

        // Modal for adding string resource
        if (showAddStringDialog) {
            ApkAddStringDialog(
                onDismiss = { showAddStringDialog = false },
                onAdd = { key, value ->
                    viewModel.addApkStringResource(key, value)
                    showAddStringDialog = false
                }
            )
        }

        // Build & Re-sign Pipeline Dialog
        if (showBuildDialog) {
            ApkBuildPipelineDialog(
                apkInfo = apkInfo,
                isBuilding = isBuilding,
                buildProgress = buildProgress,
                buildStep = buildStep,
                buildLogs = buildLogs,
                onStartBuild = { keystore, zipalign ->
                    viewModel.startApkBuildPipeline(keystore, zipalign) { result ->
                        showBuildDialog = false
                        showBuiltSuccessDialog = true
                    }
                },
                onDismiss = {
                    if (!isBuilding) showBuildDialog = false
                }
            )
        }

        // Build Success & Install Dialog
        if (showBuiltSuccessDialog) {
            builtResult?.let { built ->
                ApkBuildSuccessDialog(
                    builtApk = built,
                    onDismiss = { showBuiltSuccessDialog = false },
                    onInstallToSandbox = {
                        showBuiltSuccessDialog = false
                        viewModel.installBuiltApkAsClone(built)
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Header Bar
// -------------------------------------------------------------

@Composable
private fun ApkEditorHeader(
    apkInfo: ApkPackageInfo,
    onBack: () -> Unit,
    onRebuildApk: () -> Unit,
    onCloneDirectly: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 12.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("btn_back_from_apk_editor")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BentoTextPrimary
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        ClonedAppIcon(
            appName = apkInfo.appName,
            packageName = apkInfo.packageName,
            tintHex = apkInfo.iconColorHex,
            badgeType = apkInfo.iconBadgeType,
            badgeText = apkInfo.iconBadgeText,
            shape = apkInfo.iconShape,
            rotation = apkInfo.iconRotation,
            flipHorizontal = apkInfo.iconFlipHorizontal,
            size = 38.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = apkInfo.appName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (apkInfo.sourceType) {
                                ApkSourceType.UPLOADED_FILE -> BentoAmberContainer
                                ApkSourceType.INSTALLED_APP -> BentoIndigoContainer
                                ApkSourceType.SAMPLE_LIBRARY -> BentoCyanContainer
                            }
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = when (apkInfo.sourceType) {
                            ApkSourceType.UPLOADED_FILE -> "APK File"
                            ApkSourceType.INSTALLED_APP -> "Installed"
                            ApkSourceType.SAMPLE_LIBRARY -> "APK Lib"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (apkInfo.sourceType) {
                                ApkSourceType.UPLOADED_FILE -> BentoAmber
                                ApkSourceType.INSTALLED_APP -> BentoIndigo
                                ApkSourceType.SAMPLE_LIBRARY -> BentoCyan
                            }
                        )
                    )
                }
            }
            Text(
                text = "${apkInfo.packageName} • v${apkInfo.versionName}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = BentoTextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Build Button
        Button(
            onClick = onRebuildApk,
            modifier = Modifier
                .height(36.dp)
                .testTag("btn_build_recompile_apk"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BentoPrimary,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Rebuild",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

// -------------------------------------------------------------
// Tab 1: Properties & Manifest Flags
// -------------------------------------------------------------

@Composable
private fun ApkPropertiesTab(
    apkInfo: ApkPackageInfo,
    viewModel: AppClonerViewModel
) {
    var editAppName by remember(apkInfo.appName) { mutableStateOf(apkInfo.appName) }
    var editPackageName by remember(apkInfo.packageName) { mutableStateOf(apkInfo.packageName) }
    var editVersionName by remember(apkInfo.versionName) { mutableStateOf(apkInfo.versionName) }
    var editVersionCode by remember(apkInfo.versionCode) { mutableStateOf(apkInfo.versionCode.toString()) }
    var editMinSdk by remember(apkInfo.minSdk) { mutableStateOf(apkInfo.minSdk.toFloat()) }
    var editTargetSdk by remember(apkInfo.targetSdk) { mutableStateOf(apkInfo.targetSdk.toFloat()) }
    var editDebuggable by remember(apkInfo.isDebuggable) { mutableStateOf(apkInfo.isDebuggable) }
    var editCleartext by remember(apkInfo.allowCleartextTraffic) { mutableStateOf(apkInfo.allowCleartextTraffic) }

    fun syncChanges() {
        viewModel.updateApkProperties(
            appName = editAppName,
            packageName = editPackageName,
            versionName = editVersionName,
            versionCode = editVersionCode.toLongOrNull() ?: apkInfo.versionCode,
            minSdk = editMinSdk.toInt(),
            targetSdk = editTargetSdk.toInt(),
            isDebuggable = editDebuggable,
            allowCleartextTraffic = editCleartext
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick summary card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "APK Package Metadata",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = "File Size: ${CloneGenerator.formatBytes(apkInfo.fileSizeBytes)} • MD5: ${apkInfo.md5Checksum.take(12)}...",
                            style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary, fontSize = 11.sp)
                        )
                    }
                    Button(
                        onClick = { syncChanges() },
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer, contentColor = BentoPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // App Label & Package Name
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "App Identity & Namespace",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary)
                    )

                    OutlinedTextField(
                        value = editAppName,
                        onValueChange = {
                            editAppName = it
                            syncChanges()
                        },
                        label = { Text("App Display Name (android:label)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_apk_app_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editPackageName,
                        onValueChange = {
                            editPackageName = it
                            syncChanges()
                        },
                        label = { Text("Unique Package ID (package=\"...\")") },
                        modifier = Modifier.fillMaxWidth().testTag("input_apk_package_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Versioning and SDK Targets
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Versions & SDK Compatibility",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editVersionName,
                            onValueChange = {
                                editVersionName = it
                                syncChanges()
                            },
                            label = { Text("Version Name") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BentoPrimary,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoTextPrimary,
                                unfocusedTextColor = BentoTextPrimary
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = editVersionCode,
                            onValueChange = {
                                editVersionCode = it
                                syncChanges()
                            },
                            label = { Text("Version Code") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BentoPrimary,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoTextPrimary,
                                unfocusedTextColor = BentoTextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Target SDK API", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
                            Text("API ${editTargetSdk.toInt()} (Android 14/15)", style = MaterialTheme.typography.bodySmall.copy(color = BentoPrimary, fontWeight = FontWeight.Bold))
                        }
                        Slider(
                            value = editTargetSdk,
                            onValueChange = {
                                editTargetSdk = it
                                syncChanges()
                            },
                            valueRange = 21f..35f,
                            steps = 13,
                            colors = SliderDefaults.colors(thumbColor = BentoPrimary, activeTrackColor = BentoPrimary)
                        )
                    }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Minimum SDK API", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
                            Text("API ${editMinSdk.toInt()} (Android 8.0+)", style = MaterialTheme.typography.bodySmall.copy(color = BentoPrimary, fontWeight = FontWeight.Bold))
                        }
                        Slider(
                            value = editMinSdk,
                            onValueChange = {
                                editMinSdk = it
                                syncChanges()
                            },
                            valueRange = 19f..34f,
                            steps = 14,
                            colors = SliderDefaults.colors(thumbColor = BentoPrimary, activeTrackColor = BentoPrimary)
                        )
                    }
                }
            }
        }

        // Security & Debug Flags
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Manifest Flags & Runtime Tunnels",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Debuggable Mode", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = BentoTextPrimary))
                            Text("android:debuggable=\"true\" (Allows attaching ADB profiler)", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary, fontSize = 11.sp))
                        }
                        Switch(
                            checked = editDebuggable,
                            onCheckedChange = {
                                editDebuggable = it
                                syncChanges()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary, checkedTrackColor = BentoPrimaryContainer)
                        )
                    }

                    HorizontalDivider(color = BentoBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Allow Cleartext HTTP Traffic", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = BentoTextPrimary))
                            Text("android:usesCleartextTraffic (Bypasses TLS pinning)", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary, fontSize = 11.sp))
                        }
                        Switch(
                            checked = editCleartext,
                            onCheckedChange = {
                                editCleartext = it
                                syncChanges()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary, checkedTrackColor = BentoPrimaryContainer)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Tab 2: Permissions Manager
// -------------------------------------------------------------

@Composable
private fun ApkPermissionsTab(
    apkInfo: ApkPackageInfo,
    viewModel: AppClonerViewModel,
    onAddPermission: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPermissions = remember(apkInfo.permissions, searchQuery) {
        if (searchQuery.isBlank()) apkInfo.permissions
        else apkInfo.permissions.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter permissions...", color = BentoTextSecondary, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                singleLine = true
            )

            Button(
                onClick = onAddPermission,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        // Permissions List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPermissions, key = { it.name }) { perm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = perm.name.removePrefix("android.permission."),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (perm.isGranted) BentoTextPrimary else BentoTextMuted
                                    )
                                )
                                if (perm.isDangerous) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BentoRoseContainer)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Dangerous",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = BentoRose,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            Text(
                                text = perm.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = perm.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = perm.isGranted,
                                onCheckedChange = { viewModel.toggleApkPermission(perm.name, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BentoPrimary,
                                    checkedTrackColor = BentoPrimaryContainer
                                )
                            )
                            IconButton(
                                onClick = { viewModel.removeApkPermission(perm.name) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove permission",
                                    tint = BentoTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Tab 3: Strings Resources (i18n Editor)
// -------------------------------------------------------------

@Composable
private fun ApkStringsTab(
    apkInfo: ApkPackageInfo,
    viewModel: AppClonerViewModel,
    onAddString: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingKey by remember { mutableStateOf<String?>(null) }
    var tempEditingValue by remember { mutableStateOf("") }

    val filteredStrings = remember(apkInfo.stringResources, searchQuery) {
        if (searchQuery.isBlank()) apkInfo.stringResources
        else apkInfo.stringResources.filter {
            it.key.contains(searchQuery, ignoreCase = true) || it.value.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search string key or text...", color = BentoTextSecondary, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                singleLine = true
            )

            Button(
                onClick = onAddString,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredStrings, key = { it.key }) { res ->
                val isCurrentlyEditing = editingKey == res.key

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "<string name=\"${res.key}\">",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            Row {
                                if (isCurrentlyEditing) {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateApkStringResource(res.key, tempEditingValue)
                                            editingKey = null
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Save", tint = BentoEmerald, modifier = Modifier.size(16.dp))
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            editingKey = res.key
                                            tempEditingValue = res.value
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BentoTextSecondary, modifier = Modifier.size(15.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteApkStringResource(res.key) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BentoTextMuted, modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        }

                        if (isCurrentlyEditing) {
                            OutlinedTextField(
                                value = tempEditingValue,
                                onValueChange = { tempEditingValue = it },
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BentoPrimary,
                                    unfocusedBorderColor = BentoBorder,
                                    focusedTextColor = BentoTextPrimary,
                                    unfocusedTextColor = BentoTextPrimary
                                )
                            )
                        } else {
                            Text(
                                text = res.value,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = BentoTextPrimary
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (res.value != res.originalValue) {
                                Text(
                                    text = "Original: \"${res.originalValue}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BentoTextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Tab 4: Files & Assets Explorer
// -------------------------------------------------------------

@Composable
private fun ApkFilesTab(
    apkInfo: ApkPackageInfo,
    viewModel: AppClonerViewModel,
    onOpenFile: (ApkInternalFile) -> Unit,
    onRenameFile: (ApkInternalFile) -> Unit,
    onAddFile: () -> Unit,
    onDeleteFile: (ApkInternalFile) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredFiles = remember(apkInfo.internalFiles, searchQuery) {
        if (searchQuery.isBlank()) apkInfo.internalFiles
        else apkInfo.internalFiles.filter {
            it.relativePath.contains(searchQuery, ignoreCase = true) || it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search files in APK...", color = BentoTextSecondary, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                singleLine = true
            )

            Button(
                onClick = onAddFile,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add File", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredFiles, key = { it.id }) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = file.isEditable && file.content != null) { onOpenFile(file) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (file.fileType) {
                                            ApkFileType.MANIFEST_XML, ApkFileType.STRINGS_XML -> BentoCyanContainer
                                            ApkFileType.SMALI_CODE, ApkFileType.DEX_BYTECODE -> BentoIndigoContainer
                                            ApkFileType.DRAWABLE_IMAGE -> BentoEmeraldContainer
                                            ApkFileType.CONFIG_JSON -> BentoAmberContainer
                                            else -> BentoSurfaceAlt
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (file.fileType) {
                                        ApkFileType.MANIFEST_XML, ApkFileType.STRINGS_XML -> Icons.Default.Code
                                        ApkFileType.SMALI_CODE, ApkFileType.DEX_BYTECODE -> Icons.Default.InsertDriveFile
                                        ApkFileType.DRAWABLE_IMAGE -> Icons.Default.Image
                                        ApkFileType.CONFIG_JSON -> Icons.Default.Tune
                                        ApkFileType.NATIVE_LIB -> Icons.Default.Security
                                        else -> Icons.Default.InsertDriveFile
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = when (file.fileType) {
                                        ApkFileType.MANIFEST_XML, ApkFileType.STRINGS_XML -> BentoCyan
                                        ApkFileType.SMALI_CODE, ApkFileType.DEX_BYTECODE -> BentoIndigo
                                        ApkFileType.DRAWABLE_IMAGE -> BentoEmerald
                                        ApkFileType.CONFIG_JSON -> BentoAmber
                                        else -> BentoTextSecondary
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = file.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                )
                                Text(
                                    text = "${file.relativePath} • ${CloneGenerator.formatBytes(file.sizeBytes)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BentoTextSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (file.isEditable && file.content != null) {
                                IconButton(
                                    onClick = { onOpenFile(file) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit File Content", tint = BentoPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(
                                onClick = { onRenameFile(file) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "Rename File", tint = BentoTextSecondary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDeleteFile(file) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete File", tint = BentoTextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Tab 5: Icon Studio
// -------------------------------------------------------------

@Composable
private fun ApkIconStudioTab(
    apkInfo: ApkPackageInfo,
    viewModel: AppClonerViewModel
) {
    var badgeType by remember(apkInfo.iconBadgeType) { mutableStateOf(apkInfo.iconBadgeType) }
    var badgeText by remember(apkInfo.iconBadgeText) { mutableStateOf(apkInfo.iconBadgeText) }
    var tintHex by remember(apkInfo.iconColorHex) { mutableStateOf(apkInfo.iconColorHex) }
    var shape by remember(apkInfo.iconShape) { mutableStateOf(apkInfo.iconShape) }
    var rotation by remember(apkInfo.iconRotation) { mutableStateOf(apkInfo.iconRotation) }
    var flipHorizontal by remember(apkInfo.iconFlipHorizontal) { mutableStateOf(apkInfo.iconFlipHorizontal) }

    fun syncVisuals() {
        viewModel.updateApkVisuals(badgeType, badgeText, tintHex, shape, rotation, flipHorizontal)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Live Icon Preview Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Live Launcher Icon Preview",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ClonedAppIcon(
                        appName = apkInfo.appName,
                        packageName = apkInfo.packageName,
                        tintHex = tintHex,
                        badgeType = badgeType,
                        badgeText = badgeText,
                        shape = shape,
                        rotation = rotation,
                        flipHorizontal = flipHorizontal,
                        size = 80.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = apkInfo.appName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                }
            }
        }

        // Color Palettes
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Theme Accent Tint", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CloneGenerator.colorOptions.forEach { hex ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) {
                                BentoPrimary
                            }
                            val isSelected = tintHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        tintHex = hex
                                        syncVisuals()
                                    }
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Badge Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Badge Overlay", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary))

                    OutlinedTextField(
                        value = badgeText,
                        onValueChange = {
                            badgeText = it
                            badgeType = if (it.isBlank()) "NONE" else "TEXT"
                            syncVisuals()
                        },
                        label = { Text("Badge Label (e.g. MOD, Pro, 2, VIP, 🔒)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MOD", "PRO", "2", "VIP", "🔒", "DUAL").forEach { suggestion ->
                            OutlinedButton(
                                onClick = {
                                    badgeText = suggestion
                                    badgeType = "TEXT"
                                    syncVisuals()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(suggestion, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                            }
                        }
                    }
                }
            }
        }

        // Shape & Geometry
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Icon Geometry & Effects", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BentoPrimary))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("SQUIRCLE", "CIRCLE", "ROUNDED", "HEXAGON").forEach { shapeOption ->
                            val isSelected = shape.equals(shapeOption, ignoreCase = true)
                            Button(
                                onClick = {
                                    shape = shapeOption
                                    syncVisuals()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BentoPrimary else BentoSurfaceAlt,
                                    contentColor = if (isSelected) Color.White else BentoTextSecondary
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(shapeOption.take(5), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Flip Horizontally", style = MaterialTheme.typography.bodyMedium.copy(color = BentoTextPrimary))
                        Switch(
                            checked = flipHorizontal,
                            onCheckedChange = {
                                flipHorizontal = it
                                syncVisuals()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary, checkedTrackColor = BentoPrimaryContainer)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Modals and Dialogs
// -------------------------------------------------------------

@Composable
fun ApkFileContentEditorDialog(
    file: ApkInternalFile,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var contentText by remember(file.content) { mutableStateOf(file.content ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = BentoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = file.relativePath,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = BentoTextSecondary)
                        }
                        Button(
                            onClick = { onSave(contentText) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save File")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = BentoTextPrimary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceAlt,
                        unfocusedContainerColor = BentoSurfaceAlt
                    )
                )
            }
        }
    }
}

@Composable
fun ApkRenameFileDialog(
    file: ApkInternalFile,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(file.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = { Text("Rename File in APK", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Specify a new filename inside the APK archive:", color = BentoTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (newName.isNotBlank()) onRename(newName) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BentoTextSecondary)
            }
        }
    )
}

@Composable
fun ApkAddFileDialog(
    onDismiss: () -> Unit,
    onAdd: (relPath: String, name: String, content: String, type: ApkFileType) -> Unit
) {
    var fileName by remember { mutableStateOf("custom_hook.json") }
    var folderPath by remember { mutableStateOf("assets/") }
    var content by remember { mutableStateOf("{\n  \"hook_enabled\": true,\n  \"api_key\": \"mod_bypass_982\"\n}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = { Text("Add New File to APK", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = folderPath,
                    onValueChange = { folderPath = it },
                    label = { Text("Directory (e.g. assets/, res/raw/)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("File Content") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullPath = if (folderPath.endsWith("/")) "$folderPath$fileName" else "$folderPath/$fileName"
                    onAdd(fullPath, fileName, content, ApkFileType.CONFIG_JSON)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
            ) {
                Text("Add File")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BentoTextSecondary)
            }
        }
    )
}

@Composable
fun ApkAddPermissionDialog(
    currentPermissions: List<ApkPermissionItem>,
    onDismiss: () -> Unit,
    onAdd: (name: String, desc: String, isDanger: Boolean) -> Unit
) {
    var customName by remember { mutableStateOf("android.permission.") }
    var customDesc by remember { mutableStateOf("Custom injected permission") }
    var isDangerous by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = { Text("Add Uses-Permission", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Quick suggestions:", color = BentoTextSecondary, fontSize = 12.sp)
                LazyColumn(modifier = Modifier.height(140.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(ApkParserHelper.commonPermissionsCatalog.filter { p -> currentPermissions.none { it.name == p.name } }) { p ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    customName = p.name
                                    customDesc = p.description
                                    isDangerous = p.isDangerous
                                },
                            shape = RoundedCornerShape(6.dp),
                            color = BentoSurfaceAlt
                        ) {
                            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(p.name.removePrefix("android.permission."), color = BentoTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(if (p.isDangerous) "Dangerous" else "Normal", color = if (p.isDangerous) BentoRose else BentoEmerald, fontSize = 10.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Permission Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    )
                )

                OutlinedTextField(
                    value = customDesc,
                    onValueChange = { customDesc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (customName.isNotBlank()) onAdd(customName, customDesc, isDangerous) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
            ) {
                Text("Add Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BentoTextSecondary)
            }
        }
    )
}

@Composable
fun ApkAddStringDialog(
    onDismiss: () -> Unit,
    onAdd: (key: String, value: String) -> Unit
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = { Text("Add String Resource", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("String Key (e.g. custom_title)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Value Text") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (key.isNotBlank()) onAdd(key, value) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
            ) {
                Text("Add String")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BentoTextSecondary)
            }
        }
    )
}

@Composable
fun ApkBuildPipelineDialog(
    apkInfo: ApkPackageInfo,
    isBuilding: Boolean,
    buildProgress: Float,
    buildStep: String,
    buildLogs: List<String>,
    onStartBuild: (keystore: String, zipalign: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var keystoreType by remember { mutableStateOf("Debug Keystore (SHA-256)") }
    var zipalign by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            color = BentoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isBuilding) "Recompiling & Signing APK..." else "Rebuild & Compile APK",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                )
                Text(
                    text = "Package: ${apkInfo.packageName} • v${apkInfo.versionName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isBuilding) {
                    // Settings before build
                    Text("Signing Keystore", style = MaterialTheme.typography.labelMedium.copy(color = BentoPrimary, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    listOf("Debug Keystore (SHA-256)", "Release V1/V2/V3 Keystore", "Testkey Android Signature").forEach { keyOption ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { keystoreType = keyOption },
                            shape = RoundedCornerShape(8.dp),
                            color = if (keystoreType == keyOption) BentoPrimaryContainer else BentoSurfaceAlt,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (keystoreType == keyOption) BentoPrimary else BentoBorder
                            )
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = if (keystoreType == keyOption) BentoPrimary else BentoTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(keyOption, color = BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Enable 4-byte Zipalign Optimization", color = BentoTextPrimary, fontSize = 12.sp)
                        Switch(
                            checked = zipalign,
                            onCheckedChange = { zipalign = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BentoPrimary, checkedTrackColor = BentoPrimaryContainer)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = BentoTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onStartBuild(keystoreType, zipalign) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Recompile", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Active Build Progress & Terminal Logs
                    LinearProgressIndicator(
                        progress = { buildProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BentoPrimary,
                        trackColor = BentoSurfaceAlt
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = buildStep,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live compiler output log box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF030712),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(buildLogs) { log ->
                                Text(
                                    text = log,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = if (log.contains("SUCCESS")) BentoEmerald else if (log.contains("INIT")) BentoCyan else BentoTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApkBuildSuccessDialog(
    builtApk: ApkPackageInfo,
    onDismiss: () -> Unit,
    onInstallToSandbox: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BentoSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BentoEmerald, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("APK Recompiled & Signed!", color = BentoTextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "The modified APK package has been successfully compiled, signed with AAPT2 and packaged:",
                    color = BentoTextSecondary,
                    fontSize = 12.sp
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = BentoSurfaceAlt,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(builtApk.fileName, color = BentoCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Size: ${CloneGenerator.formatBytes(builtApk.fileSizeBytes)}", color = BentoTextSecondary, fontSize = 11.sp)
                        Text("Signature: ${builtApk.signatureScheme}", color = BentoTextSecondary, fontSize = 11.sp)
                        Text("Permissions: ${builtApk.permissions.count { it.isGranted }} active", color = BentoTextSecondary, fontSize = 11.sp)
                    }
                }

                Text(
                    text = "You can immediately install it into the isolated virtual sandbox or export the generated binary.",
                    color = BentoTextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onInstallToSandbox,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoEmerald, contentColor = Color.White)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clone & Run in Sandbox", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = BentoTextSecondary)
            }
        }
    )
}

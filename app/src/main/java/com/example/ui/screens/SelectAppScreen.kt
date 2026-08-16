package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApkPackageInfo
import com.example.model.AppCategory
import com.example.model.AppInfo
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
fun SelectAppScreen(
    viewModel: AppClonerViewModel,
    installedApps: List<AppInfo>,
    searchQuery: String,
    selectedCategory: AppCategory,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTopTab by remember { mutableIntStateOf(0) }
    val sampleApks by viewModel.sampleApks.collectAsState()

    // File picker for uploading real APK files
    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            var fileName = "uploaded_package.apk"
            var fileSize = 24_500_000L
            try {
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }
            } catch (e: Exception) {
                // Fallback name
            }
            viewModel.importApkFromFile(fileName, fileSize, it.toString())
        }
    }

    val filteredApps = remember(installedApps, searchQuery, selectedCategory) {
        installedApps.filter { app ->
            val matchesCategory = selectedCategory == AppCategory.ALL || app.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val filteredSampleApks = remember(sampleApks, searchQuery) {
        if (searchQuery.isBlank()) sampleApks
        else sampleApks.filter {
            it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true) ||
                    it.fileName.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 12.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenState.DASHBOARD) },
                    modifier = Modifier.testTag("btn_back_from_select")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Dashboard",
                        tint = BentoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Clone & APK Editor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                    Text(
                        text = if (selectedTopTab == 0) "${filteredApps.size} installed apps ready" else "${filteredSampleApks.size} APK packages & storage upload",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoTextSecondary
                        )
                    )
                }

                IconButton(
                    onClick = { viewModel.loadInstalledApps() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoSurface)
                        .border(1.dp, BentoBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh installed apps",
                        tint = BentoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Top Source Mode Tabs
            TabRow(
                selectedTabIndex = selectedTopTab,
                containerColor = BentoSurface,
                contentColor = BentoPrimary,
                divider = { HorizontalDivider(color = BentoBorder) }
            ) {
                Tab(
                    selected = selectedTopTab == 0,
                    onClick = { selectedTopTab = 0 },
                    text = {
                        Text(
                            text = "Installed Apps",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTopTab == 0) BentoPrimary else BentoTextSecondary
                            )
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selectedTopTab == 0) BentoPrimary else BentoTextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTopTab == 1,
                    onClick = { selectedTopTab = 1 },
                    text = {
                        Text(
                            text = "Import / Upload APK",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTopTab == 1) BentoPrimary else BentoTextSecondary
                            )
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selectedTopTab == 1) BentoPrimary else BentoTextMuted
                        )
                    }
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("search_app_input"),
                placeholder = {
                    Text(
                        if (selectedTopTab == 0) "Search installed apps..." else "Search APK files or packages...",
                        color = BentoTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BentoPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = BentoTextSecondary
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedContainerColor = BentoSurface,
                    unfocusedContainerColor = BentoSurface,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                singleLine = true
            )

            if (selectedTopTab == 0) {
                // Category Chips Row for Installed Apps
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AppCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            label = {
                                Text(
                                    text = category.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = BentoSurface,
                                selectedContainerColor = BentoPrimaryContainer,
                                labelColor = BentoTextSecondary,
                                selectedLabelColor = BentoPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BentoBorder,
                                selectedBorderColor = BentoPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Installed Apps List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        SelectableAppCard(
                            app = app,
                            onClone = { viewModel.prepareCloneCreation(app) },
                            onEditApk = { viewModel.openApkEditorForApp(app) }
                        )
                    }
                }
            } else {
                // APK Upload & Sample Library Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Upload / Select from Storage Hero Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, BentoPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable { apkPickerLauncher.launch("*/*") }
                                .testTag("btn_upload_apk_file"),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BentoPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload APK",
                                        tint = BentoPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Upload / Pick APK from Storage",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Select any .apk file on your device to decompile, edit properties, modify files & clone",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = BentoTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { apkPickerLauncher.launch("*/*") },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Browse", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sample APK Packages Library",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextSecondary
                                )
                            )
                            Text(
                                text = "${filteredSampleApks.size} available",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BentoTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Sample APK Cards
                    items(filteredSampleApks, key = { it.fileName }) { apk ->
                        SampleApkCard(
                            apk = apk,
                            onEditApk = { viewModel.openApkEditorForApk(apk) },
                            onCloneDirect = { viewModel.cloneDirectlyFromApk(apk) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableAppCard(
    app: AppInfo,
    onClone: () -> Unit,
    onEditApk: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
            .clickable { onClone() }
            .testTag("app_item_${app.packageName}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClonedAppIcon(
                packageName = app.packageName,
                appName = app.appName,
                category = app.category,
                size = 48.dp,
                badgeType = "NONE",
                tintHex = app.iconColorHex
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (app.isSystemApp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BentoSurfaceVariant)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "SYS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoTextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = app.packageName,
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "v${app.versionName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text("•", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextMuted))
                    Text(
                        text = CloneGenerator.formatBytes(app.sizeBytes),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Buttons: Edit APK & Clone
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // APK Editor Button
                OutlinedButton(
                    onClick = onEditApk,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Edit APK",
                        tint = BentoCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit APK", color = BentoCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                }

                // Clone Button
                Button(
                    onClick = onClone,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Clone App",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clone", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                }
            }
        }
    }
}

@Composable
fun SampleApkCard(
    apk: ApkPackageInfo,
    onEditApk: () -> Unit,
    onCloneDirect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClonedAppIcon(
                    appName = apk.appName,
                    packageName = apk.packageName,
                    tintHex = apk.iconColorHex,
                    size = 46.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = apk.appName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BentoCyanContainer)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "APK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Text(
                        text = apk.fileName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${CloneGenerator.formatBytes(apk.fileSizeBytes)} • API ${apk.targetSdk} • ${apk.permissions.size} perms • ${apk.internalFiles.size} files",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextMuted,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BentoBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditApk,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = BentoCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit APK & Files", color = BentoCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = onCloneDirect,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clone to Sandbox", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

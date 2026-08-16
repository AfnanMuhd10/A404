package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.ui.components.CloneProgressDialog
import com.example.ui.components.ClonedAppIcon
import com.example.ui.theme.BentoAmber
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneCustomizerScreen(
    viewModel: AppClonerViewModel,
    appInfo: AppInfo,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    val isCloningInProgress by viewModel.isCloningInProgress.collectAsState()

    val customCloneName by viewModel.customCloneName.collectAsState()
    val customBadgeText by viewModel.customBadgeText.collectAsState()
    val customBadgeType by viewModel.customBadgeType.collectAsState()
    val customTintHex by viewModel.customTintHex.collectAsState()
    val customShape by viewModel.customShape.collectAsState()
    val customRotation by viewModel.customRotation.collectAsState()
    val customFlipHorizontal by viewModel.customFlipHorizontal.collectAsState()

    val customDevicePreset by viewModel.customDevicePreset.collectAsState()
    val customLocationPreset by viewModel.customLocationPreset.collectAsState()
    val customSpoofLocationEnabled by viewModel.customSpoofLocationEnabled.collectAsState()
    val customIncognitoEnabled by viewModel.customIncognitoEnabled.collectAsState()
    val customPinProtection by viewModel.customPinProtection.collectAsState()
    val customPreventScreenshots by viewModel.customPreventScreenshots.collectAsState()
    val customIsolatedStorage by viewModel.customIsolatedStorage.collectAsState()
    val customAutoClearCache by viewModel.customAutoClearCache.collectAsState()

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
                    onClick = { viewModel.navigateTo(ScreenState.SELECT_APP) },
                    modifier = Modifier.testTag("btn_back_from_customizer")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Clone Studio",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                    Text(
                        text = "Customizing ${appInfo.appName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoTextSecondary
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Live Preview Card
                item {
                    LivePreviewCard(
                        packageName = appInfo.packageName,
                        appName = customCloneName.ifBlank { appInfo.appName },
                        badgeText = customBadgeText,
                        badgeType = customBadgeType,
                        tintHex = customTintHex,
                        shape = customShape,
                        rotation = customRotation,
                        flipHorizontal = customFlipHorizontal,
                        deviceModel = customDevicePreset.modelName,
                        spoofedLocation = if (customSpoofLocationEnabled) customLocationPreset.cityName else null,
                        isSecured = customPinProtection.isNotBlank()
                    )
                }

                // Section Tabs
                item {
                    val tabs = listOf("🎨 Visual Styling", "🛡️ Device Masking", "🔒 Privacy Sandbox")
                    ScrollableTabRow(
                        selectedTabIndex = activeTab,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = BentoSurface,
                        contentColor = BentoPrimary,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = activeTab == index
                            Tab(
                                selected = isSelected,
                                onClick = { activeTab = index },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BentoPrimary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
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

                // Tab Content
                when (activeTab) {
                    0 -> {
                        // VISUAL STYLING TAB
                        item {
                            StylingSection(
                                cloneName = customCloneName,
                                onCloneNameChange = { viewModel.customCloneName.value = it },
                                selectedTint = customTintHex,
                                onTintSelected = { viewModel.customTintHex.value = it },
                                selectedBadge = customBadgeText,
                                onBadgeSelected = { text, type ->
                                    viewModel.customBadgeText.value = text
                                    viewModel.customBadgeType.value = type
                                },
                                selectedShape = customShape,
                                onShapeSelected = { viewModel.customShape.value = it },
                                rotation = customRotation,
                                onRotate = {
                                    viewModel.customRotation.value = (viewModel.customRotation.value + 90f) % 360f
                                },
                                flipHorizontal = customFlipHorizontal,
                                onToggleFlip = {
                                    viewModel.customFlipHorizontal.value = !viewModel.customFlipHorizontal.value
                                }
                            )
                        }
                    }
                    1 -> {
                        // DEVICE MASKING TAB
                        item {
                            DeviceMaskingSection(
                                currentPreset = customDevicePreset,
                                onPresetSelected = { viewModel.customDevicePreset.value = it },
                                spoofLocation = customSpoofLocationEnabled,
                                onToggleSpoofLocation = { viewModel.customSpoofLocationEnabled.value = it },
                                currentLocation = customLocationPreset,
                                onLocationSelected = { viewModel.customLocationPreset.value = it }
                            )
                        }
                    }
                    2 -> {
                        // PRIVACY SANDBOX TAB
                        item {
                            PrivacySandboxSection(
                                pinProtection = customPinProtection,
                                onPinChange = { viewModel.customPinProtection.value = it },
                                incognito = customIncognitoEnabled,
                                onToggleIncognito = { viewModel.customIncognitoEnabled.value = it },
                                preventScreenshots = customPreventScreenshots,
                                onToggleScreenshots = { viewModel.customPreventScreenshots.value = it },
                                isolatedStorage = customIsolatedStorage,
                                onToggleIsolatedStorage = { viewModel.customIsolatedStorage.value = it },
                                autoClearCache = customAutoClearCache,
                                onToggleAutoClearCache = { viewModel.customAutoClearCache.value = it }
                            )
                        }
                    }
                }
            }

            // Bottom Sticky Create Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoSurface)
                    .border(1.dp, BentoBorder)
                    .padding(20.dp)
            ) {
                Button(
                    onClick = { viewModel.startCloningPipeline() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_create_clone"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Sandboxed Clone",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Animated Clone Creation Pipeline Dialog
        if (isCloningInProgress) {
            CloneProgressDialog(
                appName = appInfo.appName,
                cloneName = customCloneName.ifBlank { "${appInfo.appName} #2" },
                onComplete = { viewModel.finalizeCloneCreation() }
            )
        }
    }
}

@Composable
fun LivePreviewCard(
    packageName: String,
    appName: String,
    badgeText: String,
    badgeType: String,
    tintHex: String,
    shape: String,
    rotation: Float,
    flipHorizontal: Boolean,
    deviceModel: String,
    spoofedLocation: String?,
    isSecured: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.5.dp, BentoPrimary.copy(alpha = 0.4f), RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = BentoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LIVE CLONE PREVIEW",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated interactive preview icon
            ClonedAppIcon(
                packageName = packageName,
                appName = appName,
                size = 76.dp,
                badgeText = badgeText,
                badgeType = badgeType,
                tintHex = tintHex,
                shape = shape,
                rotation = rotation,
                flipHorizontal = flipHorizontal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = BentoTextPrimary,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simulated Specs Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BentoSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = deviceModel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }

                if (spoofedLocation != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BentoIndigoContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "GPS: $spoofedLocation",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoIndigo,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                if (isSecured) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BentoRoseContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔒 PIN LOCKED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BentoRose,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StylingSection(
    cloneName: String,
    onCloneNameChange: (String) -> Unit,
    selectedTint: String,
    onTintSelected: (String) -> Unit,
    selectedBadge: String,
    onBadgeSelected: (String, String) -> Unit,
    selectedShape: String,
    onShapeSelected: (String) -> Unit,
    rotation: Float,
    onRotate: () -> Unit,
    flipHorizontal: Boolean,
    onToggleFlip: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Clone Name Input
        OutlinedTextField(
            value = cloneName,
            onCloneNameChange,
            label = { Text("Clone App Name") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_clone_name"),
            shape = RoundedCornerShape(14.dp),
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

        // Color Tint Palette
        Column {
            Text(
                text = "Icon Color Tint Filter",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(CloneGenerator.colorOptions) { hex ->
                    val isSelected = selectedTint.equals(hex, ignoreCase = true)
                    val parsedColor = try {
                        Color(android.graphics.Color.parseColor(hex))
                    } catch (e: Exception) {
                        BentoPrimary
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) BentoPrimary else BentoBorder,
                                shape = CircleShape
                            )
                            .clickable { onTintSelected(hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Badge Selector
        Column {
            Text(
                text = "Clone Badge Identifier",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CloneGenerator.badgeOptions) { (badgeValue, label) ->
                    val isSelected = selectedBadge == badgeValue
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BentoPrimary else BentoSurface)
                            .border(1.dp, if (isSelected) BentoPrimary else BentoBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                val type = when (badgeValue) {
                                    "🔒" -> "SHIELD"
                                    "Work", "Alt", "VIP", "Dual", "Beta", "Test" -> "TEXT"
                                    else -> "NUMBER"
                                }
                                onBadgeSelected(badgeValue, type)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else BentoTextPrimary
                            )
                        )
                    }
                }
            }
        }

        // Shape Selector
        Column {
            Text(
                text = "Icon Mask Shape",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("SQUIRCLE", "CIRCLE", "ROUNDED", "HEXAGON").forEach { shape ->
                    val isSelected = selectedShape.equals(shape, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BentoPrimaryContainer else BentoSurface)
                            .border(1.dp, if (isSelected) BentoPrimary else BentoBorder, RoundedCornerShape(10.dp))
                            .clickable { onShapeSelected(shape) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = shape,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BentoPrimary else BentoTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Rotation & Flip Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onRotate,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BentoSurface, contentColor = BentoTextPrimary),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Icon(imageVector = Icons.Default.RotateRight, contentDescription = null, tint = BentoPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Rotate (${rotation.toInt()}°)", color = BentoTextPrimary)
            }

            Button(
                onClick = onToggleFlip,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (flipHorizontal) BentoPrimaryContainer else BentoSurface,
                    contentColor = BentoTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (flipHorizontal) BentoPrimary else BentoBorder)
            ) {
                Icon(imageVector = Icons.Default.Flip, contentDescription = null, tint = BentoPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (flipHorizontal) "Flipped" else "Flip", color = BentoTextPrimary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceMaskingSection(
    currentPreset: com.example.model.DevicePreset,
    onPresetSelected: (com.example.model.DevicePreset) -> Unit,
    spoofLocation: Boolean,
    onToggleSpoofLocation: (Boolean) -> Unit,
    currentLocation: com.example.model.LocationPreset,
    onLocationSelected: (com.example.model.LocationPreset) -> Unit
) {
    var deviceDropdownExpanded by remember { mutableStateOf(false) }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Device Model Selector
        Column {
            Text(
                text = "Spoofed Hardware Fingerprint",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = BentoTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = deviceDropdownExpanded,
                onExpandedChange = { deviceDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = currentPreset.modelName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deviceDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoSurface,
                        unfocusedContainerColor = BentoSurface,
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    )
                )

                ExposedDropdownMenu(
                    expanded = deviceDropdownExpanded,
                    onDismissRequest = { deviceDropdownExpanded = false }
                ) {
                    CloneGenerator.availableDevicePresets.forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(preset.modelName, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${preset.manufacturer} • ${preset.androidVersion}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BentoTextSecondary
                                    )
                                }
                            },
                            onClick = {
                                onPresetSelected(preset)
                                deviceDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // GPS Location Spoofing
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BentoIndigo,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Virtual GPS Location",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            )
                            Text(
                                text = "Simulate coordinates for social/dating/gaming",
                                style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                            )
                        }
                    }

                    Switch(
                        checked = spoofLocation,
                        onCheckedChange = onToggleSpoofLocation,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BentoIndigo,
                            checkedTrackColor = BentoIndigo.copy(alpha = 0.3f)
                        )
                    )
                }

                if (spoofLocation) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ExposedDropdownMenuBox(
                        expanded = locationDropdownExpanded,
                        onExpandedChange = { locationDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${currentLocation.cityName}, ${currentLocation.country} (${currentLocation.latitude}, ${currentLocation.longitude})",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BentoSurfaceVariant,
                                unfocusedContainerColor = BentoSurfaceVariant,
                                focusedBorderColor = BentoIndigo,
                                unfocusedBorderColor = BentoBorder,
                                focusedTextColor = BentoTextPrimary,
                                unfocusedTextColor = BentoTextPrimary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = locationDropdownExpanded,
                            onDismissRequest = { locationDropdownExpanded = false }
                        ) {
                            CloneGenerator.availableLocationPresets.forEach { loc ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${loc.cityName}, ${loc.country} (${loc.latitude}, ${loc.longitude})")
                                    },
                                    onClick = {
                                        onLocationSelected(loc)
                                        locationDropdownExpanded = false
                                    }
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
fun PrivacySandboxSection(
    pinProtection: String,
    onPinChange: (String) -> Unit,
    incognito: Boolean,
    onToggleIncognito: (Boolean) -> Unit,
    preventScreenshots: Boolean,
    onToggleScreenshots: (Boolean) -> Unit,
    isolatedStorage: Boolean,
    onToggleIsolatedStorage: (Boolean) -> Unit,
    autoClearCache: Boolean,
    onToggleAutoClearCache: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // PIN Protection Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BentoRose)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PIN Lock Protection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = "Require 4-digit code to launch this cloned space",
                            style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pinProtection,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) onPinChange(it) },
                    placeholder = { Text("Leave blank for no lock, or enter 4 digits (e.g. 1234)", color = BentoTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant,
                        focusedBorderColor = BentoRose,
                        unfocusedBorderColor = BentoBorder,
                        focusedTextColor = BentoTextPrimary,
                        unfocusedTextColor = BentoTextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // Toggles List
        PrivacyToggleItem(
            title = "Isolated Storage Container",
            subtitle = "Keep separate sandboxed databases, shared preferences, and files",
            checked = isolatedStorage,
            onCheckedChange = onToggleIsolatedStorage,
            accentColor = BentoPrimary
        )

        PrivacyToggleItem(
            title = "Stealth Incognito Mode",
            subtitle = "Hide clone icon from recent lists; access via secret vault",
            checked = incognito,
            onCheckedChange = onToggleIncognito,
            accentColor = BentoIndigo
        )

        PrivacyToggleItem(
            title = "Prevent Screenshots (FLAG_SECURE)",
            subtitle = "Block OS screencaps and screen recording of cloned instances",
            checked = preventScreenshots,
            onCheckedChange = onToggleScreenshots,
            accentColor = BentoEmerald
        )

        PrivacyToggleItem(
            title = "Auto-Purge Cache on Exit",
            subtitle = "Automatically clean temp cookies & session cache upon closing",
            checked = autoClearCache,
            onCheckedChange = onToggleAutoClearCache,
            accentColor = BentoAmber
        )
    }
}

@Composable
fun PrivacyToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}


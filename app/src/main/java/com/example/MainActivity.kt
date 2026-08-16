package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.CloneProgressDialog
import com.example.ui.components.FakeCalculatorView
import com.example.ui.screens.ApkEditorScreen
import com.example.ui.screens.CloneCustomizerScreen
import com.example.ui.screens.ClonesDashboardScreen
import com.example.ui.screens.LogsViewerScreen
import com.example.ui.screens.PrivacyVaultScreen
import com.example.ui.screens.SandboxExplorerScreen
import com.example.ui.screens.SelectAppScreen
import com.example.ui.screens.VirtualSandboxInstanceScreen
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppClonerViewModel
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

    private val viewModel: AppClonerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppClonerRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppClonerRoot(
    viewModel: AppClonerViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val clonedApps by viewModel.clonedApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val selectedAppForCloning by viewModel.selectedAppForCloning.collectAsState()
    val activeCloneInstance by viewModel.activeCloneInstance.collectAsState()
    val currentSandboxFiles by viewModel.currentSandboxFiles.collectAsState()
    val currentEditingApk by viewModel.currentEditingApk.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isCloningInProgress by viewModel.isCloningInProgress.collectAsState()
    val fakeCalculatorDisguiseActive by viewModel.fakeCalculatorDisguiseActive.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != ScreenState.DASHBOARD || fakeCalculatorDisguiseActive) {
        if (fakeCalculatorDisguiseActive) {
            viewModel.setFakeCalculatorDisguise(false)
        } else {
            when (currentScreen) {
                ScreenState.SELECT_APP,
                ScreenState.CUSTOMIZE_CLONE,
                ScreenState.VIRTUAL_SANDBOX_RUNNER,
                ScreenState.SANDBOX_EXPLORER,
                ScreenState.PRIVACY_VAULT,
                ScreenState.LOGS_VIEWER -> viewModel.navigateTo(ScreenState.DASHBOARD)
                ScreenState.APK_EDITOR -> viewModel.navigateTo(ScreenState.SELECT_APP)
                ScreenState.DASHBOARD -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .statusBarsPadding()
    ) {
        if (fakeCalculatorDisguiseActive) {
            FakeCalculatorView(
                onUnlockMaster = { viewModel.unlockStealthVault() },
                onExitDisguise = { viewModel.setFakeCalculatorDisguise(false) }
            )
        } else {
            when (currentScreen) {
                ScreenState.DASHBOARD -> {
                    ClonesDashboardScreen(
                        viewModel = viewModel,
                        clonedApps = clonedApps
                    )
                }

                ScreenState.SELECT_APP -> {
                    SelectAppScreen(
                        viewModel = viewModel,
                        installedApps = installedApps,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory
                    )
                }

                ScreenState.APK_EDITOR -> {
                    currentEditingApk?.let { apk ->
                        ApkEditorScreen(
                            viewModel = viewModel,
                            apkInfo = apk
                        )
                    } ?: run {
                        SelectAppScreen(
                            viewModel = viewModel,
                            installedApps = installedApps,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory
                        )
                    }
                }

                ScreenState.CUSTOMIZE_CLONE -> {
                    selectedAppForCloning?.let { appInfo ->
                        CloneCustomizerScreen(
                            viewModel = viewModel,
                            appInfo = appInfo
                        )
                    } ?: run {
                        ClonesDashboardScreen(
                            viewModel = viewModel,
                            clonedApps = clonedApps
                        )
                    }
                }

                ScreenState.VIRTUAL_SANDBOX_RUNNER -> {
                    activeCloneInstance?.let { clone ->
                        VirtualSandboxInstanceScreen(
                            viewModel = viewModel,
                            clone = clone
                        )
                    } ?: run {
                        ClonesDashboardScreen(
                            viewModel = viewModel,
                            clonedApps = clonedApps
                        )
                    }
                }

                ScreenState.SANDBOX_EXPLORER -> {
                    activeCloneInstance?.let { clone ->
                        SandboxExplorerScreen(
                            viewModel = viewModel,
                            clone = clone,
                            files = currentSandboxFiles
                        )
                    } ?: run {
                        ClonesDashboardScreen(
                            viewModel = viewModel,
                            clonedApps = clonedApps
                        )
                    }
                }

                ScreenState.PRIVACY_VAULT -> {
                    PrivacyVaultScreen(
                        viewModel = viewModel
                    )
                }

                ScreenState.LOGS_VIEWER -> {
                    LogsViewerScreen(
                        viewModel = viewModel,
                        logs = activityLogs
                    )
                }
            }
        }

        // Clone Generation Pipeline In-Progress Dialog
        if (isCloningInProgress) {
            CloneProgressDialog()
        }
    }
}


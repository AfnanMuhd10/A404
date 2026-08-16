package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppClonerDatabase
import com.example.data.AppClonerRepository
import com.example.data.entity.CloneActivityLog
import com.example.data.entity.ClonedApp
import com.example.model.ApkFileType
import com.example.model.ApkInternalFile
import com.example.model.ApkPackageInfo
import com.example.model.ApkPermissionItem
import com.example.model.ApkSourceType
import com.example.model.ApkStringResource
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.DevicePreset
import com.example.model.LocationPreset
import com.example.model.SandboxFileItem
import com.example.utils.ApkParserHelper
import com.example.utils.CloneGenerator
import com.example.utils.PackageManagerHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    DASHBOARD,
    SELECT_APP,
    CUSTOMIZE_CLONE,
    VIRTUAL_SANDBOX_RUNNER,
    SANDBOX_EXPLORER,
    PRIVACY_VAULT,
    LOGS_VIEWER,
    APK_EDITOR
}

class AppClonerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppClonerRepository
    val clonedApps: StateFlow<List<ClonedApp>>
    val activityLogs: StateFlow<List<CloneActivityLog>>

    private val _currentScreen = MutableStateFlow(ScreenState.DASHBOARD)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _selectedAppForCloning = MutableStateFlow<AppInfo?>(null)
    val selectedAppForCloning: StateFlow<AppInfo?> = _selectedAppForCloning.asStateFlow()

    private val _activeCloneInstance = MutableStateFlow<ClonedApp?>(null)
    val activeCloneInstance: StateFlow<ClonedApp?> = _activeCloneInstance.asStateFlow()

    private val _isCloningInProgress = MutableStateFlow(false)
    val isCloningInProgress: StateFlow<Boolean> = _isCloningInProgress.asStateFlow()

    private val _stealthModeUnlocked = MutableStateFlow(false)
    val stealthModeUnlocked: StateFlow<Boolean> = _stealthModeUnlocked.asStateFlow()

    private val _fakeCalculatorDisguiseActive = MutableStateFlow(false)
    val fakeCalculatorDisguiseActive: StateFlow<Boolean> = _fakeCalculatorDisguiseActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    // Sandbox Explorer state
    private val _currentSandboxFiles = MutableStateFlow<List<SandboxFileItem>>(emptyList())
    val currentSandboxFiles: StateFlow<List<SandboxFileItem>> = _currentSandboxFiles.asStateFlow()

    // APK Editor & Import state
    private val _sampleApks = MutableStateFlow<List<ApkPackageInfo>>(ApkParserHelper.getSampleApkList())
    val sampleApks: StateFlow<List<ApkPackageInfo>> = _sampleApks.asStateFlow()

    private val _currentEditingApk = MutableStateFlow<ApkPackageInfo?>(null)
    val currentEditingApk: StateFlow<ApkPackageInfo?> = _currentEditingApk.asStateFlow()

    private val _isApkBuilding = MutableStateFlow(false)
    val isApkBuilding: StateFlow<Boolean> = _isApkBuilding.asStateFlow()

    private val _apkBuildProgress = MutableStateFlow(0f)
    val apkBuildProgress: StateFlow<Float> = _apkBuildProgress.asStateFlow()

    private val _apkBuildStep = MutableStateFlow("")
    val apkBuildStep: StateFlow<String> = _apkBuildStep.asStateFlow()

    private val _apkBuildLogs = MutableStateFlow<List<String>>(emptyList())
    val apkBuildLogs: StateFlow<List<String>> = _apkBuildLogs.asStateFlow()

    private val _builtApkResult = MutableStateFlow<ApkPackageInfo?>(null)
    val builtApkResult: StateFlow<ApkPackageInfo?> = _builtApkResult.asStateFlow()

    // Clone Configuration builder state for current wizard
    val customCloneName = MutableStateFlow("")
    val customBadgeText = MutableStateFlow("2")
    val customBadgeType = MutableStateFlow("NUMBER") // NUMBER, TEXT, SHIELD, DOT, NONE
    val customTintHex = MutableStateFlow("#06B6D4")
    val customShape = MutableStateFlow("SQUIRCLE")
    val customRotation = MutableStateFlow(0f)
    val customFlipHorizontal = MutableStateFlow(false)
    
    // Privacy Sandbox builder options
    val customDevicePreset = MutableStateFlow(CloneGenerator.availableDevicePresets.first())
    val customLocationPreset = MutableStateFlow(CloneGenerator.availableLocationPresets.first())
    val customSpoofLocationEnabled = MutableStateFlow(false)
    val customIncognitoEnabled = MutableStateFlow(false)
    val customPinProtection = MutableStateFlow("")
    val customPreventScreenshots = MutableStateFlow(false)
    val customIsolatedStorage = MutableStateFlow(true)
    val customAutoClearCache = MutableStateFlow(false)

    init {
        val db = AppClonerDatabase.getDatabase(application)
        repository = AppClonerRepository(db.clonedAppDao(), db.cloneLogDao())

        clonedApps = repository.allClonedApps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activityLogs = repository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        loadInstalledApps()
        seedInitialClonesIfEmpty()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = PackageManagerHelper.getInstalledApps(getApplication())
            _installedApps.value = apps
        }
    }

    private fun seedInitialClonesIfEmpty() {
        viewModelScope.launch {
            // Seed a sample dual account clone if clean database
            val existing = repository.getCloneCountForPackage("com.whatsapp")
            if (existing == 0 && clonedApps.value.isEmpty()) {
                val seedClone = ClonedApp(
                    originalPackageName = "com.whatsapp",
                    clonePackageName = "com.whatsapp.clone_work",
                    cloneName = "WhatsApp (Work Dual)",
                    originalAppName = "WhatsApp",
                    cloneIndex = 1,
                    iconBadgeType = "TEXT",
                    iconBadgeText = "Work",
                    iconTintHex = "#06B6D4",
                    iconShape = "SQUIRCLE",
                    fakeAndroidId = CloneGenerator.generateRandomAndroidId(),
                    fakeModelName = "Google Pixel 9 Pro",
                    fakeMacAddress = CloneGenerator.generateRandomMacAddress(),
                    fakeImei = CloneGenerator.generateRandomImei(),
                    spoofLocation = true,
                    spoofLatitude = 35.6762,
                    spoofLongitude = 139.6503,
                    spoofLocationName = "Tokyo, Japan",
                    isolatedStorageEnabled = true,
                    mockIncomingNotifications = true,
                    launchCount = 4,
                    sandboxStorageBytes = 28_400_000L,
                    notes = "Secondary business account isolated profile"
                )
                repository.insertClonedApp(seedClone)
            }
        }
    }

    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun prepareCloneCreation(appInfo: AppInfo) {
        _selectedAppForCloning.value = appInfo
        val nextIndex = (appInfo.existingCloneCount + 1).coerceAtLeast(2)
        
        customCloneName.value = "${appInfo.appName} ($nextIndex)"
        customBadgeText.value = "$nextIndex"
        customBadgeType.value = "NUMBER"
        customTintHex.value = CloneGenerator.colorOptions.random()
        customShape.value = "SQUIRCLE"
        customRotation.value = 0f
        customFlipHorizontal.value = false
        
        customDevicePreset.value = CloneGenerator.availableDevicePresets.random()
        customLocationPreset.value = CloneGenerator.availableLocationPresets.first()
        customSpoofLocationEnabled.value = false
        customIncognitoEnabled.value = false
        customPinProtection.value = ""
        customPreventScreenshots.value = false
        customIsolatedStorage.value = true
        customAutoClearCache.value = false
        
        navigateTo(ScreenState.CUSTOMIZE_CLONE)
    }

    fun startCloningPipeline() {
        _isCloningInProgress.value = true
    }

    fun finalizeCloneCreation() {
        val app = _selectedAppForCloning.value ?: return
        viewModelScope.launch {
            val count = repository.getCloneCountForPackage(app.packageName)
            val index = count + 1
            
            val clonedApp = ClonedApp(
                originalPackageName = app.packageName,
                clonePackageName = "${app.packageName}.clone_$index",
                cloneName = customCloneName.value.ifBlank { "${app.appName} #$index" },
                originalAppName = app.appName,
                cloneIndex = index,
                iconBadgeType = customBadgeType.value,
                iconBadgeText = customBadgeText.value,
                iconTintHex = customTintHex.value,
                iconShape = customShape.value,
                iconRotation = customRotation.value,
                iconFlipHorizontal = customFlipHorizontal.value,
                fakeAndroidId = CloneGenerator.generateRandomAndroidId(),
                fakeModelName = customDevicePreset.value.modelName,
                fakeMacAddress = CloneGenerator.generateRandomMacAddress(),
                fakeImei = CloneGenerator.generateRandomImei(),
                spoofLocation = customSpoofLocationEnabled.value,
                spoofLatitude = customLocationPreset.value.latitude,
                spoofLongitude = customLocationPreset.value.longitude,
                spoofLocationName = "${customLocationPreset.value.cityName}, ${customLocationPreset.value.country}",
                isIncognito = customIncognitoEnabled.value,
                pinProtection = customPinProtection.value.takeIf { it.isNotBlank() },
                preventScreenshots = customPreventScreenshots.value,
                isolatedStorageEnabled = customIsolatedStorage.value,
                mockIncomingNotifications = true,
                autoClearCacheOnExit = customAutoClearCache.value,
                sandboxStorageBytes = 14_200_000L + (index * 1_500_000L)
            )

            val newId = repository.insertClonedApp(clonedApp)
            _isCloningInProgress.value = false
            
            // Launch the new clone instance in virtual sandbox
            val created = repository.getClonedAppById(newId)
            if (created != null) {
                launchCloneInstance(created)
            } else {
                navigateTo(ScreenState.DASHBOARD)
            }
        }
    }

    fun launchCloneInstance(clone: ClonedApp) {
        viewModelScope.launch {
            repository.recordLaunch(clone.id, clone.cloneName)
            _activeCloneInstance.value = clone
            _currentSandboxFiles.value = CloneGenerator.generateSandboxFiles(clone.clonePackageName, clone.cloneName)
            navigateTo(ScreenState.VIRTUAL_SANDBOX_RUNNER)
        }
    }

    fun stopCloneInstance(clone: ClonedApp) {
        viewModelScope.launch {
            repository.stopClone(clone.id, clone.cloneName)
            if (_activeCloneInstance.value?.id == clone.id) {
                _activeCloneInstance.value = null
                navigateTo(ScreenState.DASHBOARD)
            }
        }
    }

    fun stopAllInstances() {
        viewModelScope.launch {
            repository.stopAllClones()
            _activeCloneInstance.value = null
        }
    }

    fun rotateIdentityForClone(clone: ClonedApp) {
        viewModelScope.launch {
            val newId = CloneGenerator.generateRandomAndroidId()
            val newMac = CloneGenerator.generateRandomMacAddress()
            val newImei = CloneGenerator.generateRandomImei()
            repository.rotateIdentity(clone.id, clone.cloneName, newId, newMac, newImei)
            val updated = repository.getClonedAppById(clone.id)
            if (_activeCloneInstance.value?.id == clone.id) {
                _activeCloneInstance.value = updated
            }
        }
    }

    fun clearCloneSandbox(clone: ClonedApp) {
        viewModelScope.launch {
            repository.clearSandboxData(clone.id, clone.cloneName)
            _currentSandboxFiles.value = CloneGenerator.generateSandboxFiles(clone.clonePackageName, clone.cloneName).map {
                if (it.type == com.example.model.SandboxFileType.CACHE || it.type == com.example.model.SandboxFileType.COOKIE) {
                    it.copy(sizeBytes = 0L, previewContent = "[Flushed & Empty]")
                } else {
                    it
                }
            }
        }
    }

    fun deleteClone(clone: ClonedApp) {
        viewModelScope.launch {
            repository.deleteClonedApp(clone)
            if (_activeCloneInstance.value?.id == clone.id) {
                _activeCloneInstance.value = null
                navigateTo(ScreenState.DASHBOARD)
            }
        }
    }

    fun openSandboxExplorer(clone: ClonedApp) {
        _activeCloneInstance.value = clone
        _currentSandboxFiles.value = CloneGenerator.generateSandboxFiles(clone.clonePackageName, clone.cloneName)
        navigateTo(ScreenState.SANDBOX_EXPLORER)
    }

    fun setFakeCalculatorDisguise(active: Boolean) {
        _fakeCalculatorDisguiseActive.value = active
    }

    fun unlockStealthVault() {
        _stealthModeUnlocked.value = true
        _fakeCalculatorDisguiseActive.value = false
    }

    fun lockStealthVault() {
        _stealthModeUnlocked.value = false
    }

    // ==========================================
    // APK Editor & Import Pipeline Methods
    // ==========================================

    fun openApkEditorForApp(appInfo: AppInfo) {
        val apkInfo = ApkParserHelper.parseApkFromAppInfo(appInfo)
        _currentEditingApk.value = apkInfo
        navigateTo(ScreenState.APK_EDITOR)
    }

    fun openApkEditorForApk(apkInfo: ApkPackageInfo) {
        _currentEditingApk.value = apkInfo
        navigateTo(ScreenState.APK_EDITOR)
    }

    fun importApkFromFile(fileName: String, sizeBytes: Long, uriString: String?) {
        val parsed = ApkParserHelper.parseUploadedApk(fileName, sizeBytes, uriString)
        // Add to sample / imported list
        _sampleApks.value = listOf(parsed) + _sampleApks.value
        _currentEditingApk.value = parsed
        navigateTo(ScreenState.APK_EDITOR)
    }

    fun cloneDirectlyFromApk(apk: ApkPackageInfo) {
        // Convert APK into AppInfo and open clone wizard or clone immediately
        val tempAppInfo = AppInfo(
            packageName = apk.packageName,
            appName = apk.appName,
            versionName = apk.versionName,
            versionCode = apk.versionCode,
            isSystemApp = false,
            category = AppCategory.UTILITIES,
            sizeBytes = apk.fileSizeBytes,
            firstInstallTime = System.currentTimeMillis(),
            lastUpdateTime = System.currentTimeMillis(),
            targetSdk = apk.targetSdk,
            iconColorHex = apk.iconColorHex,
            existingCloneCount = 0
        )
        prepareCloneCreation(tempAppInfo)
    }

    fun updateApkProperties(
        appName: String,
        packageName: String,
        versionName: String,
        versionCode: Long,
        minSdk: Int,
        targetSdk: Int,
        isDebuggable: Boolean,
        allowCleartextTraffic: Boolean
    ) {
        val current = _currentEditingApk.value ?: return
        val updated = current.copy(
            appName = appName,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            minSdk = minSdk,
            targetSdk = targetSdk,
            isDebuggable = isDebuggable,
            allowCleartextTraffic = allowCleartextTraffic,
            // also update internal manifest
            internalFiles = current.internalFiles.map { file ->
                if (file.fileType == ApkFileType.MANIFEST_XML) {
                    file.copy(content = ApkParserHelper.generateGenericManifest(packageName, appName))
                } else file
            }
        )
        _currentEditingApk.value = updated
    }

    fun toggleApkPermission(permName: String, isGranted: Boolean) {
        val current = _currentEditingApk.value ?: return
        val updatedPerms = current.permissions.map {
            if (it.name == permName) it.copy(isGranted = isGranted) else it
        }
        _currentEditingApk.value = current.copy(permissions = updatedPerms)
    }

    fun addApkPermission(name: String, description: String, isDangerous: Boolean) {
        val current = _currentEditingApk.value ?: return
        if (current.permissions.none { it.name.equals(name, ignoreCase = true) }) {
            val newPerm = ApkPermissionItem(name, true, description, isDangerous)
            _currentEditingApk.value = current.copy(permissions = current.permissions + newPerm)
        }
    }

    fun removeApkPermission(name: String) {
        val current = _currentEditingApk.value ?: return
        _currentEditingApk.value = current.copy(permissions = current.permissions.filterNot { it.name == name })
    }

    fun updateApkStringResource(key: String, newValue: String) {
        val current = _currentEditingApk.value ?: return
        val updatedStrings = current.stringResources.map {
            if (it.key == key) it.copy(value = newValue) else it
        }
        val updatedFiles = current.internalFiles.map { file ->
            if (file.fileType == ApkFileType.STRINGS_XML) {
                file.copy(content = ApkParserHelper.generateStringsXml(updatedStrings))
            } else file
        }
        _currentEditingApk.value = current.copy(
            stringResources = updatedStrings,
            internalFiles = updatedFiles,
            appName = if (key == "app_name") newValue else current.appName
        )
    }

    fun addApkStringResource(key: String, value: String) {
        val current = _currentEditingApk.value ?: return
        if (current.stringResources.none { it.key == key }) {
            val newRes = ApkStringResource(key, value, value)
            val updatedStrings = current.stringResources + newRes
            val updatedFiles = current.internalFiles.map { file ->
                if (file.fileType == ApkFileType.STRINGS_XML) {
                    file.copy(content = ApkParserHelper.generateStringsXml(updatedStrings))
                } else file
            }
            _currentEditingApk.value = current.copy(
                stringResources = updatedStrings,
                internalFiles = updatedFiles
            )
        }
    }

    fun deleteApkStringResource(key: String) {
        val current = _currentEditingApk.value ?: return
        val updatedStrings = current.stringResources.filterNot { it.key == key }
        val updatedFiles = current.internalFiles.map { file ->
            if (file.fileType == ApkFileType.STRINGS_XML) {
                file.copy(content = ApkParserHelper.generateStringsXml(updatedStrings))
            } else file
        }
        _currentEditingApk.value = current.copy(
            stringResources = updatedStrings,
            internalFiles = updatedFiles
        )
    }

    fun updateApkFileContent(fileId: String, newContent: String) {
        val current = _currentEditingApk.value ?: return
        val updatedFiles = current.internalFiles.map { file ->
            if (file.id == fileId) {
                file.copy(content = newContent, sizeBytes = newContent.toByteArray().size.toLong())
            } else file
        }
        _currentEditingApk.value = current.copy(internalFiles = updatedFiles)
    }

    fun renameApkFile(fileId: String, newName: String) {
        val current = _currentEditingApk.value ?: return
        val updatedFiles = current.internalFiles.map { file ->
            if (file.id == fileId) {
                val dir = file.relativePath.substringBeforeLast('/', "")
                val newRelPath = if (dir.isEmpty()) newName else "$dir/$newName"
                file.copy(name = newName, relativePath = newRelPath)
            } else file
        }
        _currentEditingApk.value = current.copy(internalFiles = updatedFiles)
    }

    fun addApkFile(relativePath: String, name: String, content: String, fileType: ApkFileType) {
        val current = _currentEditingApk.value ?: return
        val newFile = ApkInternalFile(
            id = "f_custom_${System.currentTimeMillis()}",
            relativePath = relativePath,
            name = name,
            fileType = fileType,
            sizeBytes = content.toByteArray().size.toLong().coerceAtLeast(120L),
            content = content,
            isEditable = true
        )
        _currentEditingApk.value = current.copy(internalFiles = current.internalFiles + newFile)
    }

    fun deleteApkFile(fileId: String) {
        val current = _currentEditingApk.value ?: return
        _currentEditingApk.value = current.copy(internalFiles = current.internalFiles.filterNot { it.id == fileId })
    }

    fun updateApkVisuals(
        badgeType: String,
        badgeText: String,
        tintHex: String,
        shape: String,
        rotation: Float,
        flip: Boolean
    ) {
        val current = _currentEditingApk.value ?: return
        _currentEditingApk.value = current.copy(
            iconBadgeType = badgeType,
            iconBadgeText = badgeText,
            iconColorHex = tintHex,
            iconShape = shape,
            iconRotation = rotation,
            iconFlipHorizontal = flip
        )
    }

    fun startApkBuildPipeline(
        keystoreType: String = "Debug Keystore (SHA-256)",
        zipalign: Boolean = true,
        onComplete: (ApkPackageInfo) -> Unit
    ) {
        val current = _currentEditingApk.value ?: return
        _isApkBuilding.value = true
        _apkBuildProgress.value = 0.05f
        _apkBuildStep.value = "Parsing Modified AST & Resource Table..."
        _apkBuildLogs.value = listOf(
            "[INIT] Initializing Android Binary Resource Compiler (AAPT2)...",
            "[TARGET] ${current.packageName} (${current.versionName} - vcode ${current.versionCode})"
        )

        viewModelScope.launch {
            delay(500)
            _apkBuildProgress.value = 0.25f
            _apkBuildStep.value = "Recompiling AndroidManifest.xml & strings.xml..."
            _apkBuildLogs.value = _apkBuildLogs.value + listOf(
                "[MANIFEST] Packaging binary XML manifest with ${current.permissions.count { it.isGranted }} granted permissions",
                "[STRINGS] Compiled ${current.stringResources.size} localized string resources to resources.arsc"
            )

            delay(600)
            _apkBuildProgress.value = 0.55f
            _apkBuildStep.value = "Packing DEX bytecode & DEX optimizations..."
            _apkBuildLogs.value = _apkBuildLogs.value + listOf(
                "[DEX] Assembling Dalvik bytecode (classes.dex) with multidex support",
                "[ASSETS] Compressing ${current.internalFiles.size} assets and native libraries"
            )

            delay(600)
            _apkBuildProgress.value = 0.80f
            _apkBuildStep.value = if (zipalign) "Zipaligning 4-byte boundaries..." else "Signing APK..."
            _apkBuildLogs.value = _apkBuildLogs.value + listOf(
                "[ZIPALIGN] 4-byte memory boundary alignment verified OK",
                "[SIGNER] Injecting $keystoreType signature (Scheme V2 + V3 block)"
            )

            delay(500)
            val compiledApk = current.copy(
                fileName = "${current.appName.replace(" ", "_")}_Mod_v${current.versionName}.apk",
                md5Checksum = CloneGenerator.generateRandomAndroidId() + CloneGenerator.generateRandomAndroidId(),
                signatureScheme = "$keystoreType Verified"
            )
            _builtApkResult.value = compiledApk
            _apkBuildProgress.value = 1.0f
            _apkBuildStep.value = "Build & Sign Completed Successfully!"
            _apkBuildLogs.value = _apkBuildLogs.value + listOf(
                "[SUCCESS] Output generated: ${compiledApk.fileName} (${CloneGenerator.formatBytes(compiledApk.fileSizeBytes)})",
                "[READY] Ready to install to virtual sandbox or save to device storage"
            )
            delay(300)
            _isApkBuilding.value = false
            onComplete(compiledApk)
        }
    }

    fun installBuiltApkAsClone(builtApk: ApkPackageInfo) {
        viewModelScope.launch {
            val count = repository.getCloneCountForPackage(builtApk.packageName)
            val index = count + 1
            
            val clonedApp = ClonedApp(
                originalPackageName = builtApk.packageName,
                clonePackageName = "${builtApk.packageName}.mod_$index",
                cloneName = builtApk.appName,
                originalAppName = builtApk.appName,
                cloneIndex = index,
                iconBadgeType = builtApk.iconBadgeType.ifBlank { "TEXT" },
                iconBadgeText = builtApk.iconBadgeText.ifBlank { "MOD" },
                iconTintHex = builtApk.iconColorHex,
                iconShape = builtApk.iconShape,
                iconRotation = builtApk.iconRotation,
                iconFlipHorizontal = builtApk.iconFlipHorizontal,
                fakeAndroidId = CloneGenerator.generateRandomAndroidId(),
                fakeModelName = customDevicePreset.value.modelName,
                fakeMacAddress = CloneGenerator.generateRandomMacAddress(),
                fakeImei = CloneGenerator.generateRandomImei(),
                spoofLocation = false,
                spoofLatitude = 35.6762,
                spoofLongitude = 139.6503,
                spoofLocationName = "Tokyo, Japan",
                isIncognito = false,
                pinProtection = null,
                preventScreenshots = false,
                isolatedStorageEnabled = true,
                mockIncomingNotifications = true,
                autoClearCacheOnExit = false,
                sandboxStorageBytes = builtApk.fileSizeBytes
            )

            val newId = repository.insertClonedApp(clonedApp)
            val created = repository.getClonedAppById(newId)
            if (created != null) {
                launchCloneInstance(created)
            } else {
                navigateTo(ScreenState.DASHBOARD)
            }
        }
    }
}

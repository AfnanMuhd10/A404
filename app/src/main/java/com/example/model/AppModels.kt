package com.example.model

enum class AppCategory(val title: String) {
    ALL("All Apps"),
    SOCIAL("Social & Chat"),
    GAMES("Games"),
    PRODUCTIVITY("Productivity"),
    MEDIA("Media & Streaming"),
    UTILITIES("Tools & Utilities"),
    SYSTEM("System")
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val category: AppCategory,
    val sizeBytes: Long,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val targetSdk: Int,
    val iconColorHex: String = "#06B6D4",
    val existingCloneCount: Int = 0
)

data class DevicePreset(
    val modelName: String,
    val manufacturer: String,
    val androidVersion: String,
    val buildId: String
)

data class LocationPreset(
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class SandboxFileItem(
    val id: String,
    val name: String,
    val path: String,
    val type: SandboxFileType,
    val sizeBytes: Long,
    val modifiedTimestamp: Long,
    val previewContent: String? = null
)

enum class SandboxFileType {
    SHARED_PREF,
    DATABASE,
    CACHE,
    COOKIE,
    MEDIA_FILE,
    CERTIFICATE
}

enum class ApkSourceType {
    INSTALLED_APP,
    UPLOADED_FILE,
    SAMPLE_LIBRARY
}

enum class ApkFileType {
    MANIFEST_XML,
    STRINGS_XML,
    DRAWABLE_IMAGE,
    ASSET_FILE,
    SMALI_CODE,
    DEX_BYTECODE,
    NATIVE_LIB,
    CONFIG_JSON,
    META_INF,
    GENERIC_FILE
}

data class ApkPermissionItem(
    val name: String,
    val isGranted: Boolean,
    val description: String,
    val isDangerous: Boolean = false
)

data class ApkStringResource(
    val key: String,
    val value: String,
    val originalValue: String
)

data class ApkInternalFile(
    val id: String,
    val relativePath: String,
    val name: String,
    val fileType: ApkFileType,
    val sizeBytes: Long,
    val content: String? = null,
    val isEditable: Boolean = true
)

data class ApkPackageInfo(
    val fileName: String,
    val filePath: String? = null,
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val minSdk: Int,
    val targetSdk: Int,
    val fileSizeBytes: Long,
    val md5Checksum: String,
    val iconColorHex: String = "#06B6D4",
    val sourceType: ApkSourceType = ApkSourceType.SAMPLE_LIBRARY,
    val permissions: List<ApkPermissionItem> = emptyList(),
    val stringResources: List<ApkStringResource> = emptyList(),
    val internalFiles: List<ApkInternalFile> = emptyList(),
    val activities: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val isDebuggable: Boolean = false,
    val allowCleartextTraffic: Boolean = true,
    val signatureScheme: String = "V2 + V3 (APK Signature Scheme)",
    val iconBadgeType: String = "NONE",
    val iconBadgeText: String = "",
    val iconShape: String = "SQUIRCLE",
    val iconRotation: Float = 0f,
    val iconFlipHorizontal: Boolean = false
)

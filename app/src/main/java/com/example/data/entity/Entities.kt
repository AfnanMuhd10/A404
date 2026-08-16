package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloned_apps")
data class ClonedApp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalPackageName: String,
    val clonePackageName: String,
    val cloneName: String,
    val originalAppName: String,
    val cloneIndex: Int = 1,
    
    // Icon customization
    val iconBadgeType: String = "NUMBER", // NUMBER, TEXT, SHIELD, DOT, NONE
    val iconBadgeText: String = "2",
    val iconTintHex: String = "#06B6D4",
    val iconShape: String = "SQUIRCLE", // SQUIRCLE, CIRCLE, ROUNDED, HEXAGON
    val iconRotation: Float = 0f,
    val iconFlipHorizontal: Boolean = false,
    
    // Privacy & Spoofing Sandbox
    val fakeAndroidId: String = "",
    val fakeModelName: String = "Google Pixel 9 Pro",
    val fakeMacAddress: String = "02:42:AC:11:00:02",
    val fakeImei: String = "867530901234567",
    val spoofLocation: Boolean = false,
    val spoofLatitude: Double = 35.6762,
    val spoofLongitude: Double = 139.6503,
    val spoofLocationName: String = "Tokyo, Japan",
    val isIncognito: Boolean = false,
    val pinProtection: String? = null,
    val preventScreenshots: Boolean = false,
    val isolatedStorageEnabled: Boolean = true,
    val mockIncomingNotifications: Boolean = true,
    val autoClearCacheOnExit: Boolean = false,
    
    // Runtime Stats & Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val lastLaunchedAt: Long = 0L,
    val launchCount: Int = 0,
    val sandboxStorageBytes: Long = 14200000L, // ~14.2 MB default sandbox
    val isRunning: Boolean = false,
    val activeSessionDurationSeconds: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "clone_activity_logs")
data class CloneActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cloneId: Long,
    val cloneName: String,
    val actionType: String, // LAUNCH, DATA_CLEARED, IDENTITY_ROTATED, PIN_LOCKED, BACKUP_EXPORTED
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

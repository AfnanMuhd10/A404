package com.example.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.model.AppCategory
import com.example.model.AppInfo
import java.io.File
import kotlin.random.Random

object PackageManagerHelper {

    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val list = mutableListOf<AppInfo>()
        
        try {
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in installedApps) {
                // Filter our own app out of the source list to avoid recursive self-cloning loops
                if (app.packageName == context.packageName) continue
                
                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val label = try {
                    pm.getApplicationLabel(app).toString()
                } catch (e: Exception) {
                    app.packageName
                }
                
                val pInfo = try {
                    pm.getPackageInfo(app.packageName, 0)
                } catch (e: Exception) {
                    null
                }
                
                val versionName = pInfo?.versionName ?: "1.0.0"
                val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    pInfo?.longVersionCode ?: 1L
                } else {
                    @Suppress("DEPRECATION")
                    pInfo?.versionCode?.toLong() ?: 1L
                }
                
                val sizeBytes = try {
                    val file = File(app.sourceDir)
                    if (file.exists()) file.length() else 28_500_000L
                } catch (e: Exception) {
                    28_500_000L
                }
                
                val category = categorizeApp(app.packageName, label, isSystem)
                val iconColor = generateColorForApp(app.packageName)
                
                list.add(
                    AppInfo(
                        packageName = app.packageName,
                        appName = label,
                        versionName = versionName,
                        versionCode = versionCode,
                        isSystemApp = isSystem,
                        category = category,
                        sizeBytes = sizeBytes,
                        firstInstallTime = pInfo?.firstInstallTime ?: System.currentTimeMillis(),
                        lastUpdateTime = pInfo?.lastUpdateTime ?: System.currentTimeMillis(),
                        targetSdk = app.targetSdkVersion,
                        iconColorHex = iconColor
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If very few user apps found (e.g. running in fresh emulator), include standard popular cloneable apps
        val userAppsCount = list.count { !it.isSystemApp }
        if (userAppsCount < 5) {
            val defaults = getPresetCloneableApps()
            for (defaultApp in defaults) {
                if (list.none { it.packageName == defaultApp.packageName }) {
                    list.add(defaultApp)
                }
            }
        }

        return list.sortedWith(
            compareBy<AppInfo> { it.isSystemApp }
                .thenBy { it.appName.lowercase() }
        )
    }

    private fun categorizeApp(packageName: String, label: String, isSystem: Boolean): AppCategory {
        val lowerPkg = packageName.lowercase()
        val lowerLabel = label.lowercase()
        
        return when {
            isSystem -> AppCategory.SYSTEM
            lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") || lowerPkg.contains("messenger") ||
                    lowerPkg.contains("discord") || lowerPkg.contains("viber") || lowerPkg.contains("signal") ||
                    lowerPkg.contains("wechat") || lowerPkg.contains("line") || lowerPkg.contains("chat") -> AppCategory.SOCIAL
            lowerPkg.contains("instagram") || lowerPkg.contains("facebook") || lowerPkg.contains("twitter") ||
                    lowerPkg.contains("tiktok") || lowerPkg.contains("snapchat") || lowerPkg.contains("reddit") ||
                    lowerPkg.contains("linkedin") || lowerPkg.contains("threads") || lowerPkg.contains("social") -> AppCategory.SOCIAL
            lowerPkg.contains("game") || lowerPkg.contains("roblox") || lowerPkg.contains("pubg") ||
                    lowerPkg.contains("minecraft") || lowerPkg.contains("supercell") || lowerPkg.contains("clash") ||
                    lowerPkg.contains("genshin") || lowerPkg.contains("unity") || lowerPkg.contains("epic") -> AppCategory.GAMES
            lowerPkg.contains("slack") || lowerPkg.contains("zoom") || lowerPkg.contains("teams") ||
                    lowerPkg.contains("notion") || lowerPkg.contains("trello") || lowerPkg.contains("drive") ||
                    lowerPkg.contains("docs") || lowerPkg.contains("sheet") || lowerPkg.contains("office") -> AppCategory.PRODUCTIVITY
            lowerPkg.contains("youtube") || lowerPkg.contains("spotify") || lowerPkg.contains("netflix") ||
                    lowerPkg.contains("music") || lowerPkg.contains("video") || lowerPkg.contains("prime") ||
                    lowerPkg.contains("hulu") || lowerPkg.contains("twitch") || lowerPkg.contains("camera") -> AppCategory.MEDIA
            else -> AppCategory.UTILITIES
        }
    }

    private fun generateColorForApp(packageName: String): String {
        val colors = listOf(
            "#25D366", // WhatsApp Green
            "#0088CC", // Telegram Blue
            "#E1306C", // Instagram Pink
            "#1877F2", // Facebook Blue
            "#1DA1F2", // Twitter Blue
            "#5865F2", // Discord Blurple
            "#FF0000", // YouTube Red
            "#1DB954", // Spotify Green
            "#E50914", // Netflix Red
            "#4A154B", // Slack Aubergine
            "#06B6D4", // Cyan
            "#8B5CF6", // Violet
            "#10B981", // Emerald
            "#F59E0B"  // Amber
        )
        val hash = kotlin.math.abs(packageName.hashCode())
        return colors[hash % colors.size]
    }

    fun getPresetCloneableApps(): List<AppInfo> {
        val now = System.currentTimeMillis()
        return listOf(
            AppInfo(
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                versionName = "2.24.18.77",
                versionCode = 224187701L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 64_200_000L,
                firstInstallTime = now - 86400000L * 30,
                lastUpdateTime = now - 86400000L * 2,
                targetSdk = 34,
                iconColorHex = "#25D366"
            ),
            AppInfo(
                packageName = "org.telegram.messenger",
                appName = "Telegram",
                versionName = "10.14.5",
                versionCode = 101459L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 72_800_000L,
                firstInstallTime = now - 86400000L * 20,
                lastUpdateTime = now - 86400000L * 5,
                targetSdk = 34,
                iconColorHex = "#0088CC"
            ),
            AppInfo(
                packageName = "com.instagram.android",
                appName = "Instagram",
                versionName = "345.0.0.38",
                versionCode = 345000038L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 95_400_000L,
                firstInstallTime = now - 86400000L * 45,
                lastUpdateTime = now - 86400000L * 1,
                targetSdk = 34,
                iconColorHex = "#E1306C"
            ),
            AppInfo(
                packageName = "com.twitter.android",
                appName = "X (Twitter)",
                versionName = "10.42.0",
                versionCode = 1042000L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 58_100_000L,
                firstInstallTime = now - 86400000L * 15,
                lastUpdateTime = now - 86400000L * 4,
                targetSdk = 34,
                iconColorHex = "#1DA1F2"
            ),
            AppInfo(
                packageName = "com.discord",
                appName = "Discord",
                versionName = "240.18",
                versionCode = 240180L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 112_000_000L,
                firstInstallTime = now - 86400000L * 12,
                lastUpdateTime = now - 86400000L * 3,
                targetSdk = 34,
                iconColorHex = "#5865F2"
            ),
            AppInfo(
                packageName = "com.zhiliaoapp.musically",
                appName = "TikTok",
                versionName = "36.2.4",
                versionCode = 362400L,
                isSystemApp = false,
                category = AppCategory.SOCIAL,
                sizeBytes = 138_000_000L,
                firstInstallTime = now - 86400000L * 18,
                lastUpdateTime = now - 86400000L * 6,
                targetSdk = 34,
                iconColorHex = "#FE2C55"
            ),
            AppInfo(
                packageName = "com.spotify.music",
                appName = "Spotify",
                versionName = "8.9.68",
                versionCode = 896800L,
                isSystemApp = false,
                category = AppCategory.MEDIA,
                sizeBytes = 82_400_000L,
                firstInstallTime = now - 86400000L * 60,
                lastUpdateTime = now - 86400000L * 7,
                targetSdk = 34,
                iconColorHex = "#1DB954"
            ),
            AppInfo(
                packageName = "com.Slack",
                appName = "Slack",
                versionName = "24.08.20",
                versionCode = 2408200L,
                isSystemApp = false,
                category = AppCategory.PRODUCTIVITY,
                sizeBytes = 68_500_000L,
                firstInstallTime = now - 86400000L * 25,
                lastUpdateTime = now - 86400000L * 8,
                targetSdk = 34,
                iconColorHex = "#4A154B"
            ),
            AppInfo(
                packageName = "com.supercell.clashofclans",
                appName = "Clash of Clans",
                versionName = "16.500.12",
                versionCode = 1650012L,
                isSystemApp = false,
                category = AppCategory.GAMES,
                sizeBytes = 320_000_000L,
                firstInstallTime = now - 86400000L * 90,
                lastUpdateTime = now - 86400000L * 10,
                targetSdk = 34,
                iconColorHex = "#F59E0B"
            ),
            AppInfo(
                packageName = "com.google.android.gm",
                appName = "Gmail",
                versionName = "2024.08.04",
                versionCode = 20240804L,
                isSystemApp = true,
                category = AppCategory.PRODUCTIVITY,
                sizeBytes = 44_200_000L,
                firstInstallTime = now - 86400000L * 120,
                lastUpdateTime = now - 86400000L * 14,
                targetSdk = 34,
                iconColorHex = "#EA4335"
            )
        )
    }
}

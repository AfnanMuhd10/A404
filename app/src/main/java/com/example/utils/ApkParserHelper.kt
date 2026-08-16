package com.example.utils

import com.example.model.ApkFileType
import com.example.model.ApkInternalFile
import com.example.model.ApkPackageInfo
import com.example.model.ApkPermissionItem
import com.example.model.ApkSourceType
import com.example.model.ApkStringResource
import com.example.model.AppInfo

object ApkParserHelper {

    val commonPermissionsCatalog = listOf(
        ApkPermissionItem("android.permission.INTERNET", true, "Full Internet access", false),
        ApkPermissionItem("android.permission.ACCESS_NETWORK_STATE", true, "View network connections", false),
        ApkPermissionItem("android.permission.ACCESS_WIFI_STATE", true, "View Wi-Fi connections", false),
        ApkPermissionItem("android.permission.CAMERA", false, "Take pictures and record video", true),
        ApkPermissionItem("android.permission.RECORD_AUDIO", false, "Record audio via microphone", true),
        ApkPermissionItem("android.permission.ACCESS_FINE_LOCATION", false, "Precise GPS location", true),
        ApkPermissionItem("android.permission.ACCESS_COARSE_LOCATION", false, "Approximate network location", true),
        ApkPermissionItem("android.permission.READ_CONTACTS", false, "Read user contacts list", true),
        ApkPermissionItem("android.permission.WRITE_CONTACTS", false, "Modify user contacts", true),
        ApkPermissionItem("android.permission.READ_EXTERNAL_STORAGE", true, "Read files and media on storage", true),
        ApkPermissionItem("android.permission.WRITE_EXTERNAL_STORAGE", false, "Write files to storage", true),
        ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Post notifications to status bar", false),
        ApkPermissionItem("android.permission.VIBRATE", true, "Control vibration feedback", false),
        ApkPermissionItem("android.permission.WAKE_LOCK", true, "Prevent processor from sleeping", false),
        ApkPermissionItem("android.permission.BLUETOOTH_CONNECT", false, "Connect to paired Bluetooth devices", true),
        ApkPermissionItem("android.permission.SYSTEM_ALERT_WINDOW", false, "Draw overlay windows over other apps", true),
        ApkPermissionItem("android.permission.RECEIVE_BOOT_COMPLETED", true, "Automatically start on phone reboot", false)
    )

    fun getSampleApkList(): List<ApkPackageInfo> {
        return listOf(
            createSampleTelegramApk(),
            createSampleInstagramApk(),
            createSampleWhatsAppApk(),
            createSampleSpotifyApk(),
            createSampleNotesApk(),
            createSampleGameApk()
        )
    }

    private fun createSampleTelegramApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Full Internet access", false),
            ApkPermissionItem("android.permission.ACCESS_NETWORK_STATE", true, "View network status", false),
            ApkPermissionItem("android.permission.RECORD_AUDIO", true, "Record voice messages", true),
            ApkPermissionItem("android.permission.CAMERA", true, "Video calls & photo capture", true),
            ApkPermissionItem("android.permission.ACCESS_FINE_LOCATION", true, "Live location sharing", true),
            ApkPermissionItem("android.permission.READ_CONTACTS", true, "Sync phone contacts", true),
            ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Chat message alerts", false),
            ApkPermissionItem("android.permission.VIBRATE", true, "Haptic vibration", false)
        )

        val strings = listOf(
            ApkStringResource("app_name", "Telegram Pro", "Telegram"),
            ApkStringResource("chat_title", "Chats & Channels", "Chats"),
            ApkStringResource("settings_title", "Custom Privacy Settings", "Settings"),
            ApkStringResource("premium_badge", "Unlocked VIP Star", "Telegram Premium"),
            ApkStringResource("send_button", "Transmit", "Send"),
            ApkStringResource("call_secure_hint", "End-to-End Quantum Encrypted", "Encrypted")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 4210L, generateTelegramManifest()),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 28900L, generateStringsXml(strings)),
            ApkInternalFile("f3", "res/drawable/ic_launcher.png", "ic_launcher.png", ApkFileType.DRAWABLE_IMAGE, 14200L, null, false),
            ApkInternalFile("f4", "assets/telegram_config.json", "telegram_config.json", ApkFileType.CONFIG_JSON, 3120L, """
                {
                  "dc_endpoints": ["149.154.167.50:443", "149.154.175.100:443"],
                  "proxy_protocol": "MTPROTO_V2",
                  "max_upload_size_mb": 4096,
                  "experimental_blur": true
                }
            """.trimIndent()),
            ApkInternalFile("f5", "smali/org/telegram/ui/LaunchActivity.smali", "LaunchActivity.smali", ApkFileType.SMALI_CODE, 18900L, """
                .class public Lorg/telegram/ui/LaunchActivity;
                .super Landroidx/appcompat/app/AppCompatActivity;
                
                # Direct method invocation bypass
                .method public onCreate(Landroid/os/Bundle;)V
                    .registers 4
                    invoke-super {p0, p1}, Landroidx/appcompat/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V
                    const-string v0, "Telegram_Sandbox_Engine_Active"
                    invoke-static {v0}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I
                    return-void
                .end method
            """.trimIndent()),
            ApkInternalFile("f6", "lib/arm64-v8a/libtgvoip.so", "libtgvoip.so", ApkFileType.NATIVE_LIB, 4_190_000L, null, false),
            ApkInternalFile("f7", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 8_940_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "Telegram_v10.8.1_Dual.apk",
            appName = "Telegram Pro",
            packageName = "org.telegram.messenger.dual",
            versionName = "10.8.1",
            versionCode = 4610,
            minSdk = 24,
            targetSdk = 34,
            fileSizeBytes = 48_500_000L,
            md5Checksum = "9e107d9d372bb6826bd81d3542a419d6",
            iconColorHex = "#3B82F6",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("org.telegram.ui.LaunchActivity", "org.telegram.ui.ChatActivity", "org.telegram.ui.ProfileActivity"),
            services = listOf("org.telegram.messenger.NotificationsService", "org.telegram.messenger.VoIPService"),
            isDebuggable = false,
            allowCleartextTraffic = true
        )
    }

    private fun createSampleInstagramApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Full Internet access", false),
            ApkPermissionItem("android.permission.CAMERA", true, "Stories & Reels camera", true),
            ApkPermissionItem("android.permission.RECORD_AUDIO", true, "Reels audio recording", true),
            ApkPermissionItem("android.permission.READ_EXTERNAL_STORAGE", true, "Select photos and videos", true),
            ApkPermissionItem("android.permission.ACCESS_FINE_LOCATION", false, "Location tag recommendations", true),
            ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Direct messages & likes", false)
        )

        val strings = listOf(
            ApkStringResource("app_name", "Instagram Ultra", "Instagram"),
            ApkStringResource("direct_title", "Direct Messages (No Read Receipts)", "Messages"),
            ApkStringResource("feed_title", "Feed - Ad-Free Clean", "Feed"),
            ApkStringResource("save_media", "Download Story & Reel in HD", "Save"),
            ApkStringResource("ghost_mode", "Incognito Story Viewer Active", "View Story")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 3980L, generateInstagramManifest()),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 32100L, generateStringsXml(strings)),
            ApkInternalFile("f3", "assets/app_filters_lut.dat", "app_filters_lut.dat", ApkFileType.ASSET_FILE, 120_000L, null, false),
            ApkInternalFile("f4", "smali/com/instagram/mainactivity/InstagramMainActivity.smali", "InstagramMainActivity.smali", ApkFileType.SMALI_CODE, 14200L, """
                .class public Lcom/instagram/mainactivity/InstagramMainActivity;
                .super Landroid/app/Activity;
                
                # Ad-Blocker hook injection
                .method public isSponsoredPostBypassed()Z
                    .registers 2
                    const/4 v0, 0x1
                    return v0
                .end method
            """.trimIndent()),
            ApkInternalFile("f5", "lib/arm64-v8a/libfbjni.so", "libfbjni.so", ApkFileType.NATIVE_LIB, 2_450_000L, null, false),
            ApkInternalFile("f6", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 12_400_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "Instagram_v315.0_Ultra.apk",
            appName = "Instagram Ultra",
            packageName = "com.instagram.android.mod",
            versionName = "315.0.0",
            versionCode = 40102,
            minSdk = 26,
            targetSdk = 34,
            fileSizeBytes = 62_100_000L,
            md5Checksum = "a3f58e1c8d0e72948b8492040989f5bc",
            iconColorHex = "#EC4899",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("com.instagram.mainactivity.InstagramMainActivity", "com.instagram.direct.DirectActivity"),
            services = listOf("com.instagram.push.PushNotificationService"),
            isDebuggable = false,
            allowCleartextTraffic = false
        )
    }

    private fun createSampleWhatsAppApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Full Internet access", false),
            ApkPermissionItem("android.permission.CAMERA", true, "Photos & video calls", true),
            ApkPermissionItem("android.permission.RECORD_AUDIO", true, "Voice notes", true),
            ApkPermissionItem("android.permission.READ_CONTACTS", true, "Contact discovery", true),
            ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Incoming message alerts", false),
            ApkPermissionItem("android.permission.VIBRATE", true, "Vibration", false)
        )

        val strings = listOf(
            ApkStringResource("app_name", "WhatsApp Dual", "WhatsApp"),
            ApkStringResource("action_chats", "Dual Chats (Work Space)", "Chats"),
            ApkStringResource("status_title", "Updates & Status", "Status"),
            ApkStringResource("anti_delete", "Anti-Delete Message Guard Active", "Deleted message"),
            ApkStringResource("hide_online", "Ghost Status: Hide Online & Typing", "Online")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 3850L, generateWhatsAppManifest()),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 24500L, generateStringsXml(strings)),
            ApkInternalFile("f3", "assets/emoji_bundle.bin", "emoji_bundle.bin", ApkFileType.ASSET_FILE, 4_800_000L, null, false),
            ApkInternalFile("f4", "smali/com/whatsapp/HomeActivity.smali", "HomeActivity.smali", ApkFileType.SMALI_CODE, 12000L, """
                .class public Lcom/whatsapp/HomeActivity;
                .super Landroidx/fragment/app/FragmentActivity;
                # Dual Sandbox initialization
            """.trimIndent()),
            ApkInternalFile("f5", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 15_200_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "WhatsApp_v2.24_DualSpace.apk",
            appName = "WhatsApp Dual",
            packageName = "com.whatsapp.dual",
            versionName = "2.24.12",
            versionCode = 241200,
            minSdk = 23,
            targetSdk = 34,
            fileSizeBytes = 45_800_000L,
            md5Checksum = "7b29a8f4c01d9e2384a51167bcda4920",
            iconColorHex = "#10B981",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("com.whatsapp.HomeActivity", "com.whatsapp.Conversation"),
            services = listOf("com.whatsapp.messaging.MessageService"),
            isDebuggable = false,
            allowCleartextTraffic = true
        )
    }

    private fun createSampleSpotifyApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Stream audio data", false),
            ApkPermissionItem("android.permission.ACCESS_NETWORK_STATE", true, "Check connectivity", false),
            ApkPermissionItem("android.permission.WAKE_LOCK", true, "Background playback", false),
            ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Media playback controls", false),
            ApkPermissionItem("android.permission.BLUETOOTH_CONNECT", true, "Car & headphone audio", true)
        )

        val strings = listOf(
            ApkStringResource("app_name", "Spotify Music X", "Spotify"),
            ApkStringResource("home_title", "Discover Weekly (Unlimited Skips)", "Home"),
            ApkStringResource("premium_active", "Premium Lossless Audio Active", "Free Tier"),
            ApkStringResource("no_ads_tag", "Zero Audio Commercials", "Ad Supported")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 3600L, generateSpotifyManifest()),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 19800L, generateStringsXml(strings)),
            ApkInternalFile("f3", "assets/audio_dsp_presets.json", "audio_dsp_presets.json", ApkFileType.CONFIG_JSON, 2400L, """
                {
                  "equalizer_profile": "Bass_Boost_Ultra",
                  "sample_rate_khz": 96,
                  "bitrate_kbps": 320,
                  "crossfade_ms": 3000
                }
            """.trimIndent()),
            ApkInternalFile("f4", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 11_300_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "Spotify_v8.9_MusicX.apk",
            appName = "Spotify Music X",
            packageName = "com.spotify.music.unlimited",
            versionName = "8.9.34",
            versionCode = 893400,
            minSdk = 26,
            targetSdk = 34,
            fileSizeBytes = 39_200_000L,
            md5Checksum = "e5610d93708bbfa348bc12a559810a4e",
            iconColorHex = "#14B8A6",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("com.spotify.music.MainActivity"),
            services = listOf("com.spotify.music.service.PlaybackService"),
            isDebuggable = false,
            allowCleartextTraffic = true
        )
    }

    private fun createSampleNotesApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Cloud backup sync", false),
            ApkPermissionItem("android.permission.RECORD_AUDIO", true, "Voice transcription notes", true),
            ApkPermissionItem("android.permission.VIBRATE", true, "Checklist tactile feedback", false),
            ApkPermissionItem("android.permission.POST_NOTIFICATIONS", true, "Reminder alerts", false)
        )

        val strings = listOf(
            ApkStringResource("app_name", "Pixel Notes Pro", "Notes"),
            ApkStringResource("new_note_hint", "Write encrypted private thoughts...", "Take a note"),
            ApkStringResource("vault_label", "Biometric Markdown Vault", "Archive"),
            ApkStringResource("export_pdf", "Export to Clean PDF / Markdown", "Export")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 2800L, generateGenericManifest("com.pixel.notes.pro", "Pixel Notes Pro")),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 14200L, generateStringsXml(strings)),
            ApkInternalFile("f3", "assets/note_templates.json", "note_templates.json", ApkFileType.CONFIG_JSON, 1800L, """
                {
                  "templates": ["Cornell Notes", "Meeting Minutes", "Code Snippets", "Daily Journal"]
                }
            """.trimIndent()),
            ApkInternalFile("f4", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 4_200_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "PixelNotes_v4.2.apk",
            appName = "Pixel Notes Pro",
            packageName = "com.pixel.notes.pro",
            versionName = "4.2.0",
            versionCode = 420,
            minSdk = 26,
            targetSdk = 34,
            fileSizeBytes = 12_400_000L,
            md5Checksum = "1a8f9037c89bdf6e44b0292834c01d93",
            iconColorHex = "#F59E0B",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("com.pixel.notes.ui.MainActivity", "com.pixel.notes.ui.EditorActivity"),
            services = listOf("com.pixel.notes.sync.SyncService"),
            isDebuggable = false,
            allowCleartextTraffic = false
        )
    }

    private fun createSampleGameApk(): ApkPackageInfo {
        val permissions = listOf(
            ApkPermissionItem("android.permission.INTERNET", true, "Multiplayer matchmaking", false),
            ApkPermissionItem("android.permission.VIBRATE", true, "Haptic explosion impact", false),
            ApkPermissionItem("android.permission.WAKE_LOCK", true, "Prevent screen dim during game", false)
        )

        val strings = listOf(
            ApkStringResource("app_name", "Retro 3D Runner", "Runner"),
            ApkStringResource("play_button", "START RACE (60 FPS)", "Play"),
            ApkStringResource("score_label", "HI-SCORE MULTIPLIER x10", "Score"),
            ApkStringResource("coins_label", "UNLIMITED CYBER GEMS", "Coins")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 2600L, generateGenericManifest("com.runner.games.arcade", "Retro 3D Runner")),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 8900L, generateStringsXml(strings)),
            ApkInternalFile("f3", "assets/game_levels_pack.bin", "game_levels_pack.bin", ApkFileType.ASSET_FILE, 18_400_000L, null, false),
            ApkInternalFile("f4", "lib/arm64-v8a/libunity.so", "libunity.so", ApkFileType.NATIVE_LIB, 14_800_000L, null, false),
            ApkInternalFile("f5", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, 6_200_000L, null, false)
        )

        return ApkPackageInfo(
            fileName = "RetroRunner_v1.5_Mod.apk",
            appName = "Retro 3D Runner",
            packageName = "com.runner.games.arcade",
            versionName = "1.5.0",
            versionCode = 150,
            minSdk = 24,
            targetSdk = 34,
            fileSizeBytes = 42_800_000L,
            md5Checksum = "6c4598d1a2f90123b7e408891d09283f",
            iconColorHex = "#8B5CF6",
            sourceType = ApkSourceType.SAMPLE_LIBRARY,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("com.unity3d.player.UnityPlayerActivity"),
            services = emptyList(),
            isDebuggable = true,
            allowCleartextTraffic = true
        )
    }

    fun parseApkFromAppInfo(appInfo: AppInfo): ApkPackageInfo {
        val permissions = commonPermissionsCatalog.take(7).map {
            it.copy(isGranted = it.name == "android.permission.INTERNET" || it.name == "android.permission.POST_NOTIFICATIONS" || it.name == "android.permission.VIBRATE")
        }

        val strings = listOf(
            ApkStringResource("app_name", appInfo.appName, appInfo.appName),
            ApkStringResource("app_version", appInfo.versionName, appInfo.versionName),
            ApkStringResource("welcome_message", "Welcome to ${appInfo.appName}", "Welcome"),
            ApkStringResource("settings_title", "Configuration & Profiles", "Settings"),
            ApkStringResource("sync_status", "Isolated Sandbox Synchronized", "Online")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 3200L, generateGenericManifest(appInfo.packageName, appInfo.appName)),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 16400L, generateStringsXml(strings)),
            ApkInternalFile("f3", "res/drawable/ic_launcher.png", "ic_launcher.png", ApkFileType.DRAWABLE_IMAGE, 12800L, null, false),
            ApkInternalFile("f4", "assets/app_runtime_config.json", "app_runtime_config.json", ApkFileType.CONFIG_JSON, 1920L, """
                {
                  "package": "${appInfo.packageName}",
                  "target_sdk": ${appInfo.targetSdk},
                  "environment": "SANDBOX_VIRTUAL_CONTAINER"
                }
            """.trimIndent()),
            ApkInternalFile("f5", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, (appInfo.sizeBytes * 0.4).toLong().coerceAtLeast(2_000_000L), null, false),
            ApkInternalFile("f6", "lib/arm64-v8a/libappcore.so", "libappcore.so", ApkFileType.NATIVE_LIB, (appInfo.sizeBytes * 0.25).toLong().coerceAtLeast(1_500_000L), null, false)
        )

        return ApkPackageInfo(
            fileName = "${appInfo.appName.replace(" ", "_")}_v${appInfo.versionName}.apk",
            filePath = "/data/app/${appInfo.packageName}/base.apk",
            appName = appInfo.appName,
            packageName = appInfo.packageName,
            versionName = appInfo.versionName,
            versionCode = appInfo.versionCode,
            minSdk = 26,
            targetSdk = appInfo.targetSdk,
            fileSizeBytes = appInfo.sizeBytes,
            md5Checksum = CloneGenerator.generateRandomAndroidId() + CloneGenerator.generateRandomAndroidId(),
            iconColorHex = appInfo.iconColorHex,
            sourceType = ApkSourceType.INSTALLED_APP,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("${appInfo.packageName}.MainActivity", "${appInfo.packageName}.SettingsActivity"),
            services = listOf("${appInfo.packageName}.BackgroundService"),
            isDebuggable = false,
            allowCleartextTraffic = true
        )
    }

    fun parseUploadedApk(fileName: String, fileSizeBytes: Long, rawUriString: String?): ApkPackageInfo {
        val cleanName = fileName.removeSuffix(".apk").replace("_", " ").replace("-", " ")
        val pkgGuess = "com.custom." + cleanName.lowercase().replace(" ", "")

        val permissions = commonPermissionsCatalog.take(8).map {
            it.copy(isGranted = true)
        }

        val strings = listOf(
            ApkStringResource("app_name", cleanName, cleanName),
            ApkStringResource("title_activity_main", cleanName, cleanName),
            ApkStringResource("btn_continue", "Continue", "Continue"),
            ApkStringResource("dialog_confirm", "Confirm Action", "Confirm"),
            ApkStringResource("status_ready", "Modified APK Ready", "Ready")
        )

        val files = listOf(
            ApkInternalFile("f1", "AndroidManifest.xml", "AndroidManifest.xml", ApkFileType.MANIFEST_XML, 3100L, generateGenericManifest(pkgGuess, cleanName)),
            ApkInternalFile("f2", "res/values/strings.xml", "strings.xml", ApkFileType.STRINGS_XML, 12400L, generateStringsXml(strings)),
            ApkInternalFile("f3", "res/drawable/ic_launcher.png", "ic_launcher.png", ApkFileType.DRAWABLE_IMAGE, 16200L, null, false),
            ApkInternalFile("f4", "assets/custom_resources.json", "custom_resources.json", ApkFileType.CONFIG_JSON, 2200L, """
                {
                  "source": "Uploaded APK file",
                  "parsed_timestamp": ${System.currentTimeMillis()},
                  "storage_uri": "$rawUriString"
                }
            """.trimIndent()),
            ApkInternalFile("f5", "classes.dex", "classes.dex", ApkFileType.DEX_BYTECODE, (fileSizeBytes * 0.45).toLong().coerceAtLeast(3_000_000L), null, false),
            ApkInternalFile("f6", "lib/arm64-v8a/libnative.so", "libnative.so", ApkFileType.NATIVE_LIB, (fileSizeBytes * 0.3).toLong().coerceAtLeast(2_000_000L), null, false),
            ApkInternalFile("f7", "META-INF/CERT.RSA", "CERT.RSA", ApkFileType.META_INF, 1800L, "APK Signature v1/v2 Block", false)
        )

        return ApkPackageInfo(
            fileName = fileName,
            filePath = rawUriString,
            appName = cleanName,
            packageName = pkgGuess,
            versionName = "1.0.0",
            versionCode = 100L,
            minSdk = 26,
            targetSdk = 34,
            fileSizeBytes = fileSizeBytes.coerceAtLeast(15_400_000L),
            md5Checksum = CloneGenerator.generateRandomAndroidId() + CloneGenerator.generateRandomAndroidId(),
            iconColorHex = CloneGenerator.colorOptions.random(),
            sourceType = ApkSourceType.UPLOADED_FILE,
            permissions = permissions,
            stringResources = strings,
            internalFiles = files,
            activities = listOf("$pkgGuess.MainActivity", "$pkgGuess.HomeActivity"),
            services = listOf("$pkgGuess.PushService"),
            isDebuggable = false,
            allowCleartextTraffic = true
        )
    }

    fun generateStringsXml(strings: List<ApkStringResource>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        sb.append("<resources>\n")
        for (item in strings) {
            sb.append("    <string name=\"${item.key}\">${item.value}</string>\n")
        }
        sb.append("</resources>\n")
        return sb.toString()
    }

    fun generateGenericManifest(packageName: String, appName: String): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="$packageName"
                android:versionCode="100"
                android:versionName="1.0.0">

                <uses-permission android:name="android.permission.INTERNET" />
                <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
                <uses-permission android:name="android.permission.VIBRATE" />

                <application
                    android:allowBackup="true"
                    android:icon="@drawable/ic_launcher"
                    android:label="@string/app_name"
                    android:supportsRtl="true"
                    android:theme="@android:style/Theme.DeviceDefault.NoActionBar">
                    
                    <activity
                        android:name=".MainActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                    
                    <service
                        android:name=".BackgroundService"
                        android:exported="false" />
                </application>
            </manifest>
        """.trimIndent()
    }

    private fun generateTelegramManifest(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="org.telegram.messenger.dual"
                android:versionCode="4610"
                android:versionName="10.8.1">

                <uses-permission android:name="android.permission.INTERNET" />
                <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                <uses-permission android:name="android.permission.RECORD_AUDIO" />
                <uses-permission android:name="android.permission.CAMERA" />
                <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
                <uses-permission android:name="android.permission.VIBRATE" />

                <application
                    android:name="org.telegram.messenger.ApplicationLoader"
                    android:allowBackup="false"
                    android:icon="@drawable/ic_launcher"
                    android:label="@string/app_name"
                    android:hardwareAccelerated="true"
                    android:largeHeap="true">
                    
                    <activity
                        android:name="org.telegram.ui.LaunchActivity"
                        android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
                        android:exported="true"
                        android:windowSoftInputMode="adjustResize">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent()
    }

    private fun generateInstagramManifest(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="com.instagram.android.mod"
                android:versionCode="40102"
                android:versionName="315.0.0">

                <uses-permission android:name="android.permission.INTERNET" />
                <uses-permission android:name="android.permission.CAMERA" />
                <uses-permission android:name="android.permission.RECORD_AUDIO" />
                <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

                <application
                    android:name="com.instagram.app.InstagramApp"
                    android:icon="@drawable/ic_launcher"
                    android:label="@string/app_name"
                    android:hardwareAccelerated="true">
                    
                    <activity
                        android:name="com.instagram.mainactivity.InstagramMainActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent()
    }

    private fun generateWhatsAppManifest(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="com.whatsapp.dual"
                android:versionCode="241200"
                android:versionName="2.24.12">

                <uses-permission android:name="android.permission.INTERNET" />
                <uses-permission android:name="android.permission.CAMERA" />
                <uses-permission android:name="android.permission.RECORD_AUDIO" />
                <uses-permission android:name="android.permission.READ_CONTACTS" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

                <application
                    android:icon="@drawable/ic_launcher"
                    android:label="@string/app_name">
                    
                    <activity
                        android:name="com.whatsapp.HomeActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent()
    }

    private fun generateSpotifyManifest(): String {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="com.spotify.music.unlimited"
                android:versionCode="893400"
                android:versionName="8.9.34">

                <uses-permission android:name="android.permission.INTERNET" />
                <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                <uses-permission android:name="android.permission.WAKE_LOCK" />
                <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

                <application
                    android:icon="@drawable/ic_launcher"
                    android:label="@string/app_name">
                    
                    <activity
                        android:name="com.spotify.music.MainActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent()
    }
}

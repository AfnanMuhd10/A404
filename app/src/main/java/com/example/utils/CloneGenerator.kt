package com.example.utils

import com.example.model.DevicePreset
import com.example.model.LocationPreset
import com.example.model.SandboxFileItem
import com.example.model.SandboxFileType
import java.security.SecureRandom

object CloneGenerator {

    private val charPool: List<Char> = ('a'..'f') + ('0'..'9')

    fun generateRandomAndroidId(): String {
        return (1..16)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    fun generateRandomMacAddress(): String {
        val random = SecureRandom()
        val bytes = ByteArray(6)
        random.nextBytes(bytes)
        bytes[0] = (bytes[0].toInt() or 0x02 and 0xFE.toByte().toInt()).toByte() // local unicast MAC
        return bytes.joinToString(":") { "%02X".format(it) }
    }

    fun generateRandomImei(): String {
        val builder = StringBuilder("86")
        for (i in 1..13) {
            builder.append(kotlin.random.Random.nextInt(0, 10))
        }
        return builder.toString()
    }

    val availableDevicePresets = listOf(
        DevicePreset("Google Pixel 9 Pro", "Google", "Android 15", "AP2A.240805.005"),
        DevicePreset("Samsung Galaxy S24 Ultra", "Samsung", "Android 14 (OneUI 6.1)", "UP1A.231005.007"),
        DevicePreset("Nothing Phone (2)", "Nothing", "Android 14 (Nothing OS 2.6)", "UKQ1.230924.001"),
        DevicePreset("Xiaomi 14 Ultra", "Xiaomi", "Android 14 (HyperOS)", "UKQ1.231003.002"),
        DevicePreset("OnePlus 12", "OnePlus", "Android 14 (OxygenOS 14)", "CPH2581_14.0.0.802"),
        DevicePreset("Sony Xperia 1 VI", "Sony", "Android 14", "69.0.A.2.32"),
        DevicePreset("ASUS ROG Phone 8 Pro", "ASUS", "Android 14 (ROG UI)", "34.1420.1420.316")
    )

    val availableLocationPresets = listOf(
        LocationPreset("Tokyo", "Japan", 35.6762, 139.6503),
        LocationPreset("New York", "United States", 40.7128, -74.0060),
        LocationPreset("London", "United Kingdom", 51.5074, -0.1278),
        LocationPreset("Zurich", "Switzerland", 47.3769, 8.5417),
        LocationPreset("Singapore", "Singapore", 1.3521, 103.8198),
        LocationPreset("Dubai", "United Arab Emirates", 25.2048, 55.2708),
        LocationPreset("Sydney", "Australia", -33.8688, 151.2093),
        LocationPreset("Berlin", "Germany", 52.5200, 13.4050)
    )

    val colorOptions = listOf(
        "#06B6D4", // Cyan
        "#3B82F6", // Blue
        "#8B5CF6", // Purple
        "#EC4899", // Pink
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#EF4444", // Red
        "#6366F1", // Indigo
        "#14B8A6", // Teal
        "#64748B"  // Slate
    )

    val badgeOptions = listOf(
        "2" to "Numbered 2",
        "3" to "Numbered 3",
        "Work" to "Work Profile",
        "Alt" to "Alternate",
        "VIP" to "VIP Account",
        "🔒" to "Stealth Lock",
        "Dual" to "Dual Space",
        "Beta" to "Beta Profile",
        "Test" to "Sandboxed"
    )

    fun generateSandboxFiles(clonePackageName: String, cloneName: String): List<SandboxFileItem> {
        val now = System.currentTimeMillis()
        return listOf(
            SandboxFileItem(
                id = "sp_user_session",
                name = "user_session_vault.xml",
                path = "/data/data/$clonePackageName/shared_prefs/user_session_vault.xml",
                type = SandboxFileType.SHARED_PREF,
                sizeBytes = 4280L,
                modifiedTimestamp = now - 120_000L,
                previewContent = """
                    <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
                    <map>
                        <string name="account_token">eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</string>
                        <string name="user_id">clone_user_${generateRandomAndroidId().take(6)}</string>
                        <boolean name="is_sandbox_isolated" value="true" />
                        <string name="device_fingerprint">${generateRandomAndroidId()}</string>
                        <int name="session_login_count" value="12" />
                    </map>
                """.trimIndent()
            ),
            SandboxFileItem(
                id = "db_isolated",
                name = "sandboxed_store.db",
                path = "/data/data/$clonePackageName/databases/sandboxed_store.db",
                type = SandboxFileType.DATABASE,
                sizeBytes = 6_820_000L,
                modifiedTimestamp = now - 350_000L,
                previewContent = """
                    SQLite format 3
                    Table: messages (id INT PRIMARY KEY, peer_id TEXT, text_content BLOB, timestamp BIGINT)
                    Table: contacts (contact_id TEXT, display_name TEXT, avatar_uri TEXT)
                    Table: media_cache_index (hash TEXT, uri TEXT, size_bytes INT)
                    Indexes: 4 active B-Tree indices initialized.
                """.trimIndent()
            ),
            SandboxFileItem(
                id = "cookies_jar",
                name = "isolated_cookies.dat",
                path = "/data/data/$clonePackageName/app_webview/Cookies",
                type = SandboxFileType.COOKIE,
                sizeBytes = 18_400L,
                modifiedTimestamp = now - 500_000L,
                previewContent = """
                    # Netscape HTTP Cookie File - Isolated Virtual Sandbox Container
                    .app.internal	TRUE	/	TRUE	1755290400	session_cookie_alt	sec_${generateRandomAndroidId().take(12)}
                    .auth.provider	TRUE	/	TRUE	1755290400	oauth2_token	oa2_${generateRandomAndroidId().take(16)}
                """.trimIndent()
            ),
            SandboxFileItem(
                id = "cache_thumbnails",
                name = "cached_media_blobs.bin",
                path = "/data/data/$clonePackageName/cache/image_cache/cached_media_blobs.bin",
                type = SandboxFileType.CACHE,
                sizeBytes = 7_400_000L,
                modifiedTimestamp = now - 60_000L,
                previewContent = "Binary Cache Store [7.4 MB] - Encrypted Virtual Storage Layer"
            ),
            SandboxFileItem(
                id = "sec_cert",
                name = "virtual_keystore.bks",
                path = "/data/data/$clonePackageName/app_security/virtual_keystore.bks",
                type = SandboxFileType.CERTIFICATE,
                sizeBytes = 8_192L,
                modifiedTimestamp = now - 1_200_000L,
                previewContent = "RSA 4096-bit Virtual Hardware Root Key - Hardware Attestation Bypass Active"
            )
        )
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return "%.1f %s".format(value, units[digitGroups.coerceAtMost(units.size - 1)])
    }
}

package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CloneActivityLog
import com.example.data.entity.ClonedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface ClonedAppDao {

    @Query("SELECT * FROM cloned_apps ORDER BY createdAt DESC")
    fun getAllClonedApps(): Flow<List<ClonedApp>>

    @Query("SELECT * FROM cloned_apps WHERE isIncognito = 0 ORDER BY createdAt DESC")
    fun getVisibleClonedApps(): Flow<List<ClonedApp>>

    @Query("SELECT * FROM cloned_apps WHERE id = :id")
    suspend fun getClonedAppById(id: Long): ClonedApp?

    @Query("SELECT * FROM cloned_apps WHERE id = :id")
    fun observeClonedAppById(id: Long): Flow<ClonedApp?>

    @Query("SELECT * FROM cloned_apps WHERE originalPackageName = :pkg")
    suspend fun getClonesForPackage(pkg: String): List<ClonedApp>

    @Query("SELECT COUNT(*) FROM cloned_apps WHERE originalPackageName = :pkg")
    suspend fun getCloneCountForPackage(pkg: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClonedApp(clonedApp: ClonedApp): Long

    @Update
    suspend fun updateClonedApp(clonedApp: ClonedApp)

    @Delete
    suspend fun deleteClonedApp(clonedApp: ClonedApp)

    @Query("DELETE FROM cloned_apps WHERE id = :id")
    suspend fun deleteClonedAppById(id: Long)

    @Query("UPDATE cloned_apps SET isRunning = :running WHERE id = :id")
    suspend fun updateRunningState(id: Long, running: Boolean)

    @Query("UPDATE cloned_apps SET isRunning = 0")
    suspend fun stopAllClones()

    @Query("UPDATE cloned_apps SET lastLaunchedAt = :timestamp, launchCount = launchCount + 1, isRunning = 1 WHERE id = :id")
    suspend fun recordLaunch(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE cloned_apps SET sandboxStorageBytes = :bytes WHERE id = :id")
    suspend fun updateStorageBytes(id: Long, bytes: Long)

    @Query("UPDATE cloned_apps SET fakeAndroidId = :newId, fakeMacAddress = :newMac, fakeImei = :newImei WHERE id = :id")
    suspend fun rotateIdentity(id: Long, newId: String, newMac: String, newImei: String)
}

@Dao
interface CloneLogDao {

    @Query("SELECT * FROM clone_activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<CloneActivityLog>>

    @Query("SELECT * FROM clone_activity_logs WHERE cloneId = :cloneId ORDER BY timestamp DESC LIMIT 50")
    fun getLogsForClone(cloneId: Long): Flow<List<CloneActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CloneActivityLog)

    @Query("DELETE FROM clone_activity_logs WHERE cloneId = :cloneId")
    suspend fun clearLogsForClone(cloneId: Long)

    @Query("DELETE FROM clone_activity_logs")
    suspend fun clearAllLogs()
}

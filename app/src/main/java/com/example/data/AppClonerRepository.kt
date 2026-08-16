package com.example.data

import com.example.data.dao.CloneLogDao
import com.example.data.dao.ClonedAppDao
import com.example.data.entity.CloneActivityLog
import com.example.data.entity.ClonedApp
import kotlinx.coroutines.flow.Flow

class AppClonerRepository(
    private val clonedAppDao: ClonedAppDao,
    private val cloneLogDao: CloneLogDao
) {
    val allClonedApps: Flow<List<ClonedApp>> = clonedAppDao.getAllClonedApps()
    val visibleClonedApps: Flow<List<ClonedApp>> = clonedAppDao.getVisibleClonedApps()
    val allLogs: Flow<List<CloneActivityLog>> = cloneLogDao.getAllLogs()

    suspend fun getClonedAppById(id: Long): ClonedApp? = clonedAppDao.getClonedAppById(id)
    
    fun observeClonedAppById(id: Long): Flow<ClonedApp?> = clonedAppDao.observeClonedAppById(id)

    suspend fun getCloneCountForPackage(pkg: String): Int = clonedAppDao.getCloneCountForPackage(pkg)

    suspend fun insertClonedApp(app: ClonedApp): Long {
        val id = clonedAppDao.insertClonedApp(app)
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = id,
                cloneName = app.cloneName,
                actionType = "CLONED",
                description = "Created clone container ${app.clonePackageName} with ID: ${app.fakeAndroidId.take(8)}..."
            )
        )
        return id
    }

    suspend fun updateClonedApp(app: ClonedApp) {
        clonedAppDao.updateClonedApp(app)
    }

    suspend fun deleteClonedApp(app: ClonedApp) {
        clonedAppDao.deleteClonedApp(app)
        cloneLogDao.clearLogsForClone(app.id)
    }

    suspend fun recordLaunch(id: Long, appName: String) {
        clonedAppDao.recordLaunch(id)
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = id,
                cloneName = appName,
                actionType = "LAUNCH",
                description = "Launched sandboxed instance in isolated virtual environment."
            )
        )
    }

    suspend fun stopClone(id: Long, appName: String) {
        clonedAppDao.updateRunningState(id, false)
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = id,
                cloneName = appName,
                actionType = "STOPPED",
                description = "Virtual instance container stopped and memory released."
            )
        )
    }

    suspend fun stopAllClones() {
        clonedAppDao.stopAllClones()
    }

    suspend fun rotateIdentity(id: Long, appName: String, newAndroidId: String, newMac: String, newImei: String) {
        clonedAppDao.rotateIdentity(id, newAndroidId, newMac, newImei)
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = id,
                cloneName = appName,
                actionType = "IDENTITY_ROTATED",
                description = "Regenerated device fingerprint. New Android ID: ${newAndroidId.take(8)}..."
            )
        )
    }

    suspend fun clearSandboxData(id: Long, appName: String) {
        // Reset storage size to base 2.4 MB
        clonedAppDao.updateStorageBytes(id, 2400000L)
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = id,
                cloneName = appName,
                actionType = "DATA_CLEARED",
                description = "Flushed sandbox cache, cookies, and temporary data artifacts."
            )
        )
    }

    suspend fun logCustomAction(cloneId: Long, cloneName: String, actionType: String, description: String) {
        cloneLogDao.insertLog(
            CloneActivityLog(
                cloneId = cloneId,
                cloneName = cloneName,
                actionType = actionType,
                description = description
            )
        )
    }
}

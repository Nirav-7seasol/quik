
package com.messages.readmms.readsmss.feature.backup

import com.messages.readmms.readsmss.model.BackupFile
import com.messages.readmms.readsmss.repository.BackupRepository

data class BackupState(
    val backupProgress: BackupRepository.Progress = BackupRepository.Progress.Idle(),
    val restoreProgress: BackupRepository.Progress = BackupRepository.Progress.Idle(),
    val lastBackup: String = "",
    val backups: List<BackupFile> = listOf(),
    val upgraded: Boolean = true
)

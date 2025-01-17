
package com.messages.readmms.readsmss.model

data class BackupFile(
    val path: String,
    val date: Long,
    val messages: Int,
    val size: Long
)

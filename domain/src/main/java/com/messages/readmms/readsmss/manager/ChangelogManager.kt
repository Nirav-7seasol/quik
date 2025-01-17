
package com.messages.readmms.readsmss.manager

interface ChangelogManager {

    data class CumulativeChangelog(
        val added: List<String>,
        val improved: List<String>,
        val removed: List<String>,
        val fixed: List<String>
    )

    /**
     * Returns true if the app has benn updated since the last time this method was called
     */
    fun didUpdate(): Boolean

    suspend fun getChangelog(): CumulativeChangelog

    fun markChangelogSeen()

}

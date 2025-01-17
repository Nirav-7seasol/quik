
package com.messages.readmms.readsmss.manager

import android.content.Context
import android.util.Log
import com.messages.readmms.readsmss.common.util.extensions.versionCode
import com.messages.readmms.readsmss.util.Preferences
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ChangelogManagerImpl @Inject constructor(
    private val context: Context,
    private val moshi: Moshi,
    private val prefs: Preferences
) : ChangelogManager {

    override fun didUpdate(): Boolean = (prefs.changelogVersion.get() ?: 0) != context.versionCode

    override suspend fun getChangelog(): ChangelogManager.CumulativeChangelog {
        val listType = Types.newParameterizedType(List::class.java, Changeset::class.java)
        val adapter = moshi.adapter<List<Changeset>>(listType)

        return withContext(Dispatchers.IO) {
            try {
                val changelogs = context.assets.open("changelog.json").bufferedReader().use { it.readText() }
                        .let(adapter::fromJson)
                        .orEmpty()
                        .sortedBy { changelog -> changelog.versionCode }
                        .filter { changelog ->
                            changelog.versionCode in (prefs.changelogVersion.get() ?: 0).inc()..context.versionCode
                        }

                ChangelogManager.CumulativeChangelog(
                        added = changelogs.fold(listOf()) { acc, changelog -> acc + changelog.added.orEmpty()},
                        improved = changelogs.fold(listOf()) { acc, changelog -> acc + changelog.improved.orEmpty()},
                        removed = changelogs.fold(listOf()) { acc, changelog -> acc + changelog.removed.orEmpty()},
                        fixed = changelogs.fold(listOf()) { acc, changelog -> acc + changelog.fixed.orEmpty()}
                )

            } catch (e: Exception) {
                Log.e("ChangelogManager", "Error parsing changelog JSON", e)
                ChangelogManager.CumulativeChangelog(emptyList(), emptyList(), emptyList(), emptyList())
            }
        }
    }

    override fun markChangelogSeen() {
        prefs.changelogVersion.set(context.versionCode)
    }

    @JsonClass(generateAdapter = false)
    data class Changeset(
        @Json(name = "added") val added: List<String>?,
        @Json(name = "improved") val improved: List<String>?,
        @Json(name = "removed") val removed: List<String>?,
        @Json(name = "fixed") val fixed: List<String>?,
        @Json(name = "versionName") val versionName: String,
        @Json(name = "versionCode") val versionCode: Int
    )

}

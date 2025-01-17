
package com.messages.readmms.readsmss.feature.changelog

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messages.readmms.readsmss.BuildConfig
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.feature.main.MainActivity
import com.messages.readmms.readsmss.manager.ChangelogManager
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.changelog_dialog.view.*

class ChangelogDialog(activity: MainActivity) {

    val moreClicks: Subject<Unit> = PublishSubject.create()

    private val dialog: AlertDialog
    private val adapter = ChangelogAdapter(activity)

    init {
        val layout = LayoutInflater.from(activity).inflate(R.layout.changelog_dialog, null)

        dialog = MaterialAlertDialogBuilder(activity)
                .setCancelable(true)
                .setView(layout)
                .create()

        layout.version.text = activity.getString(R.string.changelog_version, BuildConfig.VERSION_NAME)
        layout.changelog.adapter = adapter
        layout.more.setOnClickListener { dialog.dismiss(); moreClicks.onNext(Unit) }
        layout.dismiss.setOnClickListener { dialog.dismiss() }
    }

    fun show(changelog: ChangelogManager.CumulativeChangelog) {
        adapter.setChangelog(changelog)
        dialog.show()
    }

}

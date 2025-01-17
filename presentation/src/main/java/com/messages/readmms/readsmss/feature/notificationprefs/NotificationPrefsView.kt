
package com.messages.readmms.readsmss.feature.notificationprefs

import android.net.Uri
import com.messages.readmms.readsmss.common.base.QkView
import com.messages.readmms.readsmss.common.widget.PreferenceView
import io.reactivex.Observable
import io.reactivex.subjects.Subject

interface NotificationPrefsView : QkView<NotificationPrefsState> {

    val preferenceClickIntent: Subject<PreferenceView>
    val previewModeSelectedIntent: Subject<Int>
    val ringtoneSelectedIntent: Observable<String>
    val actionsSelectedIntent: Subject<Int>

    fun showPreviewModeDialog()
    fun showRingtonePicker(default: Uri?)
    fun showActionDialog(selected: Int)
}

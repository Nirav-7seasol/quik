
package com.messages.readmms.readsmss.common.util.extensions

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.subjects.Subject

fun MaterialAlertDialogBuilder.setPositiveButton(@StringRes textId: Int, subject: Subject<Unit>): MaterialAlertDialogBuilder {
    return setPositiveButton(textId) { _, _ -> subject.onNext(Unit) }
}

fun MaterialAlertDialogBuilder.setNegativeButton(@StringRes textId: Int, subject: Subject<Unit>): MaterialAlertDialogBuilder {
    return setNegativeButton(textId) { _, _ -> subject.onNext(Unit) }
}

fun MaterialAlertDialogBuilder.setNeutralButton(@StringRes textId: Int, subject: Subject<Unit>): MaterialAlertDialogBuilder {
    return setNeutralButton(textId) { _, _ -> subject.onNext(Unit) }
}

fun AlertDialog.setShowing(show: Boolean) {
    if (isShowing && !show) {
        dismiss()
    } else if (!isShowing && show) {
        show()
    }
}

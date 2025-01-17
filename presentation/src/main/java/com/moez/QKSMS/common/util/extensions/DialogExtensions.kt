/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.octoshrimpy.quik.common.util.extensions

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

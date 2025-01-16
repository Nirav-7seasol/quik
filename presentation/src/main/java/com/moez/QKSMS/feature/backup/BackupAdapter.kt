/*
 * Copyright (C) 2017 Moez Bhatti <innovate.bhatti@gmail.com>
 *
 * This file is part of replify.
 *
 * replify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * replify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with replify.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moez.QKSMS.feature.backup

import android.content.Context
import android.text.format.Formatter
import android.view.ViewGroup
import com.moez.QKSMS.common.base.FlowableAdapterWithBinding
import com.moez.QKSMS.common.base.QkViewHolderWithBinding
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.base.FlowableAdapter
import dev.octoshrimpy.quik.common.base.QkViewHolder
import dev.octoshrimpy.quik.common.util.DateFormatter
import dev.octoshrimpy.quik.databinding.BackupListItemBinding
import dev.octoshrimpy.quik.model.BackupFile
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class BackupAdapter @Inject constructor(
    private val context: Context,
    private val dateFormatter: DateFormatter
) : FlowableAdapterWithBinding<BackupFile, BackupListItemBinding>() {

    val backupSelected: Subject<BackupFile> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolderWithBinding<BackupListItemBinding> {
        return QkViewHolderWithBinding(parent, BackupListItemBinding::inflate).apply {
            binding.root.setOnClickListener { backupSelected.onNext(getItem(adapterPosition)) }
        }
    }

    override fun onBindViewHolder(holder: QkViewHolderWithBinding<BackupListItemBinding>, position: Int) {
        val backup = getItem(position)
        val count = backup.messages

        holder.binding.title.text = dateFormatter.getDetailedTimestamp(backup.date)
        holder.binding.messages.text = context.resources.getQuantityString(R.plurals.backup_message_count, count, count)
        holder.binding.size.text = Formatter.formatFileSize(context, backup.size)
    }

}
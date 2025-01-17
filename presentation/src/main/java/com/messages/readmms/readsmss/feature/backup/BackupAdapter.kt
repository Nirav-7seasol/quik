
package com.messages.readmms.readsmss.feature.backup

import android.content.Context
import android.text.format.Formatter
import android.view.ViewGroup
import com.messages.readmms.readsmss.common.base.FlowableAdapterWithBinding
import com.messages.readmms.readsmss.common.base.QkViewHolderWithBinding
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.base.FlowableAdapter
import com.messages.readmms.readsmss.common.base.QkViewHolder
import com.messages.readmms.readsmss.common.util.DateFormatter
import com.messages.readmms.readsmss.databinding.BackupListItemBinding
import com.messages.readmms.readsmss.model.BackupFile
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
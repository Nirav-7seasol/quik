
package com.messages.readmms.readsmss.feature.compose.part

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.messages.readmms.readsmss.common.base.QkAdapter
import com.messages.readmms.readsmss.common.base.QkViewHolder
import com.messages.readmms.readsmss.common.util.Colors
import com.messages.readmms.readsmss.common.util.extensions.forwardTouches
import com.messages.readmms.readsmss.extensions.isSmil
import com.messages.readmms.readsmss.extensions.isText
import com.messages.readmms.readsmss.feature.compose.BubbleUtils.canGroup
import com.messages.readmms.readsmss.model.Message
import com.messages.readmms.readsmss.model.MmsPart
import io.reactivex.Observable
import kotlinx.android.synthetic.main.message_list_item_in.*
import javax.inject.Inject

class PartsAdapter @Inject constructor(
    colors: Colors,
    fileBinder: FileBinder,
    mediaBinder: MediaBinder,
    vCardBinder: VCardBinder
) : QkAdapter<MmsPart>() {

    private val partBinders = listOf(mediaBinder, vCardBinder, fileBinder)

    var theme: Colors.Theme = colors.theme()
        set(value) {
            field = value
            partBinders.forEach { binder -> binder.theme = value }
        }

    val clicks: Observable<Long> = Observable.merge(partBinders.map { it.clicks })

    private lateinit var message: Message
    private var previous: Message? = null
    private var next: Message? = null
    private var holder: QkViewHolder? = null
    private var bodyVisible: Boolean = true

    fun setData(message: Message, previous: Message?, next: Message?, holder: QkViewHolder) {
        this.message = message
        this.previous = previous
        this.next = next
        this.holder = holder
        this.bodyVisible = holder.body.visibility == View.VISIBLE
        this.data = message.parts.filter { !it.isSmil() && !it.isText() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QkViewHolder {
        val layout = partBinders.getOrNull(viewType)?.partLayout ?: 0
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        holder?.containerView?.let(view::forwardTouches)
        return QkViewHolder(view)
    }

    override fun onBindViewHolder(holder: QkViewHolder, position: Int) {
        val part = data[position]

        val canGroupWithPrevious = canGroup(message, previous) || position > 0
        val canGroupWithNext = canGroup(message, next) || position < itemCount - 1 || bodyVisible

        partBinders
                .firstOrNull { it.canBindPart(part) }
                ?.bindPart(holder, part, message, canGroupWithPrevious, canGroupWithNext)
    }

    override fun getItemViewType(position: Int): Int {
        val part = data[position]
        return partBinders.indexOfFirst { it.canBindPart(part) }
    }

}
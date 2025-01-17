
package com.messages.readmms.readsmss.feature.compose.part

import android.content.Context
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.base.QkViewHolder
import com.messages.readmms.readsmss.common.util.Colors
import com.messages.readmms.readsmss.common.util.extensions.setVisible
import com.messages.readmms.readsmss.common.widget.BubbleImageView
import com.messages.readmms.readsmss.extensions.isImage
import com.messages.readmms.readsmss.extensions.isVideo
import com.messages.readmms.readsmss.model.Message
import com.messages.readmms.readsmss.model.MmsPart
import com.messages.readmms.readsmss.util.GlideApp
import kotlinx.android.synthetic.main.mms_preview_list_item.*
import javax.inject.Inject

class MediaBinder @Inject constructor(colors: Colors, private val context: Context) : PartBinder() {

    override val partLayout = R.layout.mms_preview_list_item
    override var theme = colors.theme()

    override fun canBindPart(part: MmsPart) = part.isImage() || part.isVideo()

    override fun bindPart(
        holder: QkViewHolder,
        part: MmsPart,
        message: Message,
        canGroupWithPrevious: Boolean,
        canGroupWithNext: Boolean
    ) {
        holder.video.setVisible(part.isVideo())
        holder.containerView.setOnClickListener { clicks.onNext(part.id) }

        holder.thumbnail.bubbleStyle = when {
            !canGroupWithPrevious && canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_FIRST else BubbleImageView.Style.IN_FIRST
            canGroupWithPrevious && canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_MIDDLE else BubbleImageView.Style.IN_MIDDLE
            canGroupWithPrevious && !canGroupWithNext -> if (message.isMe()) BubbleImageView.Style.OUT_LAST else BubbleImageView.Style.IN_LAST
            else -> BubbleImageView.Style.ONLY
        }

        GlideApp.with(context).load(part.getUri()).fitCenter().into(holder.thumbnail)
    }

}
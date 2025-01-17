
package com.messages.readmms.readsmss.feature.compose

import com.messages.readmms.readsmss.compat.SubscriptionInfoCompat
import com.messages.readmms.readsmss.model.Attachment
import com.messages.readmms.readsmss.model.Conversation
import com.messages.readmms.readsmss.model.Message
import com.messages.readmms.readsmss.model.Recipient
import io.realm.RealmResults

data class ComposeState(
    val hasError: Boolean = false,
    val editingMode: Boolean = false,
    val threadId: Long = 0,
    val selectedChips: List<Recipient> = ArrayList(),
    val sendAsGroup: Boolean = true,
    val isGroup: Boolean = false,
    val conversationtitle: String = "",
    val conversationEnable: Boolean = false,
    val loading: Boolean = false,
    val query: String = "",
    val searchSelectionId: Long = -1,
    val searchSelectionPosition: Int = 0,
    val searchResults: Int = 0,
    val messages: Pair<Conversation, RealmResults<Message>>? = null,
    val selectedMessages: Int = 0,
    val scheduled: Long = 0,
    val attachments: List<Attachment> = ArrayList(),
    val attaching: Boolean = false,
    val remaining: String = "",
    val subscription: SubscriptionInfoCompat? = null,
    val canSend: Boolean = false
)
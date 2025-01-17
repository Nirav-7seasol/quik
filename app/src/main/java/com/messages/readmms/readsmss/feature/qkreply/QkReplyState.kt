
package com.messages.readmms.readsmss.feature.qkreply

import com.messages.readmms.readsmss.compat.SubscriptionInfoCompat
import com.messages.readmms.readsmss.model.Conversation
import com.messages.readmms.readsmss.model.Message
import io.realm.RealmResults

data class QkReplyState(
    val hasError: Boolean = false,
    val threadId: Long = 0,
    val title: String = "",
    val expanded: Boolean = false,
    val data: Pair<Conversation, RealmResults<Message>>? = null,
    val remaining: String = "",
    val subscription: SubscriptionInfoCompat? = null,
    val canSend: Boolean = false
)
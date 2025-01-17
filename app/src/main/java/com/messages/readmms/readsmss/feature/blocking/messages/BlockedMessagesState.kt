
package com.messages.readmms.readsmss.feature.blocking.messages

import com.messages.readmms.readsmss.model.Conversation
import io.realm.RealmResults

data class BlockedMessagesState(
    val data: RealmResults<Conversation>? = null,
    val selected: Int = 0
)

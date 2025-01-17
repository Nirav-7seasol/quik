
package com.messages.readmms.readsmss.feature.scheduled

import com.messages.readmms.readsmss.model.ScheduledMessage
import io.realm.RealmResults

data class ScheduledState(
    val scheduledMessages: RealmResults<ScheduledMessage>? = null,
    val upgraded: Boolean = true
)

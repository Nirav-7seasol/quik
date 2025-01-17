
package com.messages.readmms.readsmss.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.messages.readmms.readsmss.interactor.SendScheduledMessage
import com.messages.readmms.readsmss.repository.MessageRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class SendScheduledMessageReceiver : BroadcastReceiver() {

    @Inject lateinit var messageRepo: MessageRepository
    @Inject lateinit var sendScheduledMessage: SendScheduledMessage

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        intent.getLongExtra("id", -1L).takeIf { it >= 0 }?.let { id ->
            val result = goAsync()
            sendScheduledMessage.execute(id) { result.finish() }
        }
    }

}
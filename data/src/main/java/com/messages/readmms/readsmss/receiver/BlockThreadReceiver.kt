
package com.messages.readmms.readsmss.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.messages.readmms.readsmss.blocking.BlockingClient
import com.messages.readmms.readsmss.interactor.MarkBlocked
import com.messages.readmms.readsmss.repository.ConversationRepository
import com.messages.readmms.readsmss.util.Preferences
import dagger.android.AndroidInjection
import javax.inject.Inject

class BlockThreadReceiver : BroadcastReceiver() {

    @Inject lateinit var blockingClient: BlockingClient
    @Inject lateinit var conversationRepo: ConversationRepository
    @Inject lateinit var markBlocked: MarkBlocked
    @Inject lateinit var prefs: Preferences

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        val pendingResult = goAsync()
        val threadId = intent.getLongExtra("threadId", 0)
        val conversation = conversationRepo.getConversation(threadId)!!
        val blockingManager = prefs.blockingManager.get()

        blockingClient
                .block(conversation.recipients.map { it.address })
                .andThen(markBlocked.buildObservable(MarkBlocked.Params(listOf(threadId), blockingManager, null)))
                .subscribe { pendingResult.finish() }
    }

}


package com.messages.readmms.readsmss.interactor

import com.messages.readmms.readsmss.repository.ConversationRepository
import com.messages.readmms.readsmss.repository.MessageRepository
import com.messages.readmms.readsmss.util.Preferences
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class DeleteOldMessages @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val prefs: Preferences
) : Interactor<Unit>() {

    override fun buildObservable(params: Unit): Flowable<*> = Flowable.fromCallable {
        val maxAge = prefs.autoDelete.get().takeIf { it > 0 } ?: return@fromCallable
        val counts = messageRepo.getOldMessageCounts(maxAge)
        val threadIds = counts.keys.toLongArray()

        Timber.d("Deleting ${counts.values.sum()} old messages from ${threadIds.size} conversations")
        messageRepo.deleteOldMessages(maxAge)
        conversationRepo.updateConversations(*threadIds)
    }

}

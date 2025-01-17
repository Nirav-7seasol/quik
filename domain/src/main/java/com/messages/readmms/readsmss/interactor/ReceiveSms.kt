
package com.messages.readmms.readsmss.interactor

import android.telephony.SmsMessage
import com.messages.readmms.readsmss.blocking.BlockingClient
import com.messages.readmms.readsmss.extensions.mapNotNull
import com.messages.readmms.readsmss.manager.NotificationManager
import com.messages.readmms.readsmss.manager.ShortcutManager
import com.messages.readmms.readsmss.repository.ConversationRepository
import com.messages.readmms.readsmss.repository.MessageRepository
import com.messages.readmms.readsmss.util.Preferences
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class ReceiveSms @Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val blockingClient: BlockingClient,
    private val prefs: Preferences,
    private val messageRepo: MessageRepository,
    private val notificationManager: NotificationManager,
    private val updateBadge: UpdateBadge,
    private val shortcutManager: ShortcutManager
) : Interactor<ReceiveSms.Params>() {

    class Params(val subId: Int, val messages: Array<SmsMessage>)

    override fun buildObservable(params: Params): Flowable<*> {
        return Flowable.just(params)
                .filter { it.messages.isNotEmpty() }
                .mapNotNull {
                    // Don't continue if the sender is blocked
                    val messages = it.messages
                    val address = messages[0].displayOriginatingAddress
                    val action = blockingClient.shouldBlock(address).blockingGet()
                    val shouldDrop = prefs.drop.get()
                    Timber.v("block=$action, drop=$shouldDrop")

                    // If we should drop the message, don't even save it
                    if (action is BlockingClient.Action.Block && shouldDrop) {
                        return@mapNotNull null
                    }

                    val time = messages[0].timestampMillis
                    val body: String = messages
                            .mapNotNull { message -> message.displayMessageBody }
                            .reduce { body, new -> body + new }

                    // Add the message to the db
                    val message = messageRepo.insertReceivedSms(it.subId, address, body, time)

                    when (action) {
                        is BlockingClient.Action.Block -> {
                            messageRepo.markRead(message.threadId)
                            conversationRepo.markBlocked(listOf(message.threadId), prefs.blockingManager.get(), action.reason)
                        }
                        is BlockingClient.Action.Unblock -> conversationRepo.markUnblocked(message.threadId)
                        else -> Unit
                    }

                    message
                }
                .doOnNext { message ->
                    conversationRepo.updateConversations(message.threadId) // Update the conversation
                }
                .mapNotNull { message ->
                    conversationRepo.getOrCreateConversation(message.threadId) // Map message to conversation
                }
                .filter { conversation -> !conversation.blocked } // Don't notify for blocked conversations
                .doOnNext { conversation ->
                    // Unarchive conversation if necessary
                    if (conversation.archived) conversationRepo.markUnarchived(conversation.id)
                }
                .map { conversation -> conversation.id } // Map to the id because [delay] will put us on the wrong thread
                .doOnNext { threadId -> notificationManager.update(threadId) } // Update the notification
                .doOnNext { shortcutManager.updateShortcuts() } // Update shortcuts
                .flatMap { updateBadge.buildObservable(Unit) } // Update the badge and widget
    }

}

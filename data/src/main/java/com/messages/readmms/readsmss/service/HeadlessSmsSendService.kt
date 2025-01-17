
package com.messages.readmms.readsmss.service

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import com.messages.readmms.readsmss.interactor.SendMessage
import com.messages.readmms.readsmss.repository.ConversationRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class HeadlessSmsSendService : IntentService("HeadlessSmsSendService") {

    @Inject lateinit var conversationRepo: ConversationRepository
    @Inject lateinit var sendMessage: SendMessage

    override fun onHandleIntent(intent: Intent?) {
        if (intent?.action != TelephonyManager.ACTION_RESPOND_VIA_MESSAGE) return

        AndroidInjection.inject(this)
        intent.extras?.getString(Intent.EXTRA_TEXT)?.takeIf { it.isNotBlank() }?.let { body ->
            val intentUri = intent.data
            val recipients = intentUri?.let(::getRecipients)?.split(";") ?: return@let
            val threadId = conversationRepo.getOrCreateConversation(recipients)?.id ?: 0L
            sendMessage.execute(SendMessage.Params(-1, threadId, recipients, body))
        }
    }

    private fun getRecipients(uri: Uri): String {
        val base = uri.schemeSpecificPart
        val position = base.indexOf('?')
        return if (position == -1) base else base.substring(0, position)
    }

}

package com.messages.readmms.readsmss.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.Telephony
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.messages.readmms.readsmss.extensions.forEach
import com.messages.readmms.readsmss.extensions.insertOrUpdate
import com.messages.readmms.readsmss.extensions.map
import com.messages.readmms.readsmss.manager.KeyManager
import com.messages.readmms.readsmss.mapper.CursorToContact
import com.messages.readmms.readsmss.mapper.CursorToContactGroup
import com.messages.readmms.readsmss.mapper.CursorToContactGroupMember
import com.messages.readmms.readsmss.mapper.CursorToConversation
import com.messages.readmms.readsmss.mapper.CursorToMessage
import com.messages.readmms.readsmss.mapper.CursorToPart
import com.messages.readmms.readsmss.mapper.CursorToRecipient
import com.messages.readmms.readsmss.model.Contact
import com.messages.readmms.readsmss.model.ContactGroup
import com.messages.readmms.readsmss.model.Conversation
import com.messages.readmms.readsmss.model.Message
import com.messages.readmms.readsmss.model.MmsPart
import com.messages.readmms.readsmss.model.PhoneNumber
import com.messages.readmms.readsmss.model.Recipient
import com.messages.readmms.readsmss.model.SyncLog
import com.messages.readmms.readsmss.util.PhoneNumberUtils
import com.messages.readmms.readsmss.util.tryOrNull
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val conversationRepo: ConversationRepository,
    private val cursorToConversation: CursorToConversation,
    private val cursorToMessage: CursorToMessage,
    private val cursorToPart: CursorToPart,
    private val cursorToRecipient: CursorToRecipient,
    private val cursorToContact: CursorToContact,
    private val cursorToContactGroup: CursorToContactGroup,
    private val cursorToContactGroupMember: CursorToContactGroupMember,
    private val keys: KeyManager,
    private val phoneNumberUtils: PhoneNumberUtils,
    private val rxPrefs: RxSharedPreferences
) : SyncRepository {

    override val syncProgress: Subject<SyncRepository.SyncProgress> =
            BehaviorSubject.createDefault(SyncRepository.SyncProgress.Idle)

    override fun syncMessages() {

        // If the sync is already running, don't try to do another one
        if (syncProgress.blockingFirst() is SyncRepository.SyncProgress.Running) return
        syncProgress.onNext(SyncRepository.SyncProgress.Running(0, 0, true))

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val persistedData = realm.copyFromRealm(realm.where(Conversation::class.java)
                .beginGroup()
                .equalTo("archived", true)
                .or()
                .equalTo("blocked", true)
                .or()
                .equalTo("pinned", true)
                .or()
                .isNotEmpty("name")
                .or()
                .isNotNull("blockingClient")
                .or()
                .isNotEmpty("blockReason")
                .endGroup()
                .findAll())
                .associateBy { conversation -> conversation.id }
                .toMutableMap()

        realm.delete(Contact::class.java)
        realm.delete(ContactGroup::class.java)
        realm.delete(Conversation::class.java)
        realm.delete(Message::class.java)
        realm.delete(MmsPart::class.java)
        realm.delete(Recipient::class.java)

        keys.reset()

        val partsCursor = cursorToPart.getPartsCursor()
        val messageCursor = cursorToMessage.getMessagesCursor()
        val conversationCursor = cursorToConversation.getConversationsCursor()
        val recipientCursor = cursorToRecipient.getRecipientCursor()

        val max = (partsCursor?.count ?: 0) +
                (messageCursor?.count ?: 0) +
                (conversationCursor?.count ?: 0) +
                (recipientCursor?.count ?: 0)

        var progress = 0

        // Sync message parts
        partsCursor?.use {
            partsCursor.forEach {
                tryOrNull {
                    progress++
                    val part = cursorToPart.map(partsCursor)
                    realm.insertOrUpdate(part)
                }
            }
        }

        // Sync messages
        messageCursor?.use {
            val messageColumns = CursorToMessage.MessageColumns(messageCursor)
            messageCursor.forEach { cursor ->
                tryOrNull {
                    progress++
                    syncProgress.onNext(SyncRepository.SyncProgress.Running(max, progress, false))
                    val message = cursorToMessage.map(Pair(cursor, messageColumns)).apply {
                        if (isMms()) {
                            parts = RealmList<MmsPart>().apply {
                                addAll(realm.where(MmsPart::class.java)
                                        .equalTo("messageId", contentId)
                                        .findAll())
                            }
                        }
                    }
                    realm.insertOrUpdate(message)
                }
            }
        }

        // Migrate blocked conversations from 2.7.3
        val oldBlockedSenders = rxPrefs.getStringSet("pref_key_blocked_senders")
        oldBlockedSenders.get()
                .map { threadIdString -> threadIdString.toLong() }
                .filter { threadId -> !persistedData.contains(threadId) }
                .forEach { threadId -> persistedData[threadId] = Conversation(id = threadId, blocked = true) }

        // Sync conversations
        conversationCursor?.use {
            conversationCursor.forEach { cursor ->
                tryOrNull {
                    progress++
                    syncProgress.onNext(SyncRepository.SyncProgress.Running(max, progress, false))
                    val conversation = cursorToConversation.map(cursor).apply {
                        persistedData[id]?.let { persistedConversation ->
                            archived = persistedConversation.archived
                            blocked = persistedConversation.blocked
                            pinned = persistedConversation.pinned
                            name = persistedConversation.name
                            blockingClient = persistedConversation.blockingClient
                            blockReason = persistedConversation.blockReason
                        }
                        lastMessage = realm.where(Message::class.java)
                                .sort("date", Sort.DESCENDING)
                                .equalTo("threadId", id)
                                .findFirst()
                    }
                    realm.insertOrUpdate(conversation)
                }
            }
        }

        // Sync recipients
        recipientCursor?.use {
            val contacts = realm.copyToRealmOrUpdate(getContacts())
            recipientCursor.forEach { cursor ->
                tryOrNull {
                    progress++
                    syncProgress.onNext(SyncRepository.SyncProgress.Running(max, progress, false))
                    val recipient = cursorToRecipient.map(cursor).apply {
                        contact = contacts.firstOrNull { contact ->
                            contact.numbers.any { phoneNumberUtils.compare(address, it.address) }
                        }
                    }
                    realm.insertOrUpdate(recipient)
                }
            }
        }

        syncProgress.onNext(SyncRepository.SyncProgress.Running(0, 0, true))


        realm.insert(SyncLog())
        realm.commitTransaction()
        realm.close()

        // Only delete this after the sync has successfully completed
        oldBlockedSenders.delete()

        syncProgress.onNext(SyncRepository.SyncProgress.Idle)
    }

    override fun syncMessage(uri: Uri): Message? {

        // If we don't have a valid type, return null
        val type = when {
            uri.toString().contains("mms") -> "mms"
            uri.toString().contains("sms") -> "sms"
            else -> return null
        }

        // If we don't have a valid id, return null
        val id = tryOrNull(false) { ContentUris.parseId(uri) } ?: return null

        // Check if the message already exists, so we can reuse the id
        val existingId = Realm.getDefaultInstance().use { realm ->
            realm.refresh()
            realm.where(Message::class.java)
                    .equalTo("type", type)
                    .equalTo("contentId", id)
                    .findFirst()
                    ?.id
        }

        // The uri might be something like content://mms/inbox/id
        // The box might change though, so we should just use the mms/id uri
        val stableUri = when (type) {
            "mms" -> ContentUris.withAppendedId(Telephony.Mms.CONTENT_URI, id)
            else -> ContentUris.withAppendedId(Telephony.Sms.CONTENT_URI, id)
        }

        return contentResolver.query(stableUri, null, null, null, null)?.use { cursor ->

            // If there are no rows, return null. Otherwise, we've moved to the first row
            if (!cursor.moveToFirst()) return null

            val columnsMap = CursorToMessage.MessageColumns(cursor)
            cursorToMessage.map(Pair(cursor, columnsMap)).apply {
                existingId?.let { this.id = it }

                if (isMms()) {
                    parts = RealmList<MmsPart>().apply {
                        addAll(cursorToPart.getPartsCursor(contentId)?.map { cursorToPart.map(it) }.orEmpty())
                    }
                }

                conversationRepo.getOrCreateConversation(threadId)
                insertOrUpdate()
            }
        }
    }

    override fun syncContacts() {
        // Load all the contacts
        var contacts = getContacts()

        Realm.getDefaultInstance()?.use { realm ->
            val recipients = realm.where(Recipient::class.java).findAll()

            realm.executeTransaction {
                realm.delete(Contact::class.java)
                realm.delete(ContactGroup::class.java)

                contacts = realm.copyToRealmOrUpdate(contacts)
                realm.insertOrUpdate(getContactGroups(contacts))

                // Update all the recipients with the new contacts
                recipients.forEach { recipient ->
                    recipient.contact = contacts.find { contact ->
                        contact.numbers.any { phoneNumberUtils.compare(recipient.address, it.address) }
                    }
                }

                realm.insertOrUpdate(recipients)
            }

        }
    }

    private fun getContacts(): List<Contact> {
        val defaultNumberIds = Realm.getDefaultInstance().use { realm ->
            realm.where(PhoneNumber::class.java)
                    .equalTo("isDefault", true)
                    .findAll()
                    .map { number -> number.id }
        }

        return cursorToContact.getContactsCursor()
                ?.map { cursor -> cursorToContact.map(cursor) }
                ?.groupBy { contact -> contact.lookupKey }
                ?.map { contacts ->
                    // Sometimes, contacts providers on the phone will create duplicate phone number entries. This
                    // commonly happens with Whatsapp. Let's try to detect these duplicate entries and filter them out
                    val uniqueNumbers = mutableListOf<PhoneNumber>()
                    contacts.value
                            .flatMap { it.numbers }
                            .forEach { number ->
                                number.isDefault = defaultNumberIds.any { id -> id == number.id }
                                val duplicate = uniqueNumbers.find { other ->
                                    phoneNumberUtils.compare(number.address, other.address)
                                }

                                if (duplicate == null) {
                                    uniqueNumbers += number
                                } else if (!duplicate.isDefault && number.isDefault) {
                                    duplicate.isDefault = true
                                }
                            }

                    contacts.value.first().apply {
                        numbers.clear()
                        numbers.addAll(uniqueNumbers)
                    }
                } ?: listOf()
    }

    private fun getContactGroups(contacts: List<Contact>): List<ContactGroup> {
        val groupMembers = cursorToContactGroupMember.getGroupMembersCursor()
                ?.map(cursorToContactGroupMember::map)
                .orEmpty()

        val groups = cursorToContactGroup.getContactGroupsCursor()
                ?.map(cursorToContactGroup::map)
                .orEmpty()

        groups.forEach { group ->
            group.contacts.addAll(groupMembers
                    .filter { member -> member.groupId == group.id }
                    .mapNotNull { member -> contacts.find { contact -> contact.lookupKey == member.lookupKey } })
        }

        return groups
    }

}


package com.messages.readmms.readsmss.feature.compose.editing

import com.messages.readmms.readsmss.model.Contact
import com.messages.readmms.readsmss.model.ContactGroup
import com.messages.readmms.readsmss.model.Conversation
import com.messages.readmms.readsmss.model.PhoneNumber
import io.realm.RealmList

sealed class ComposeItem {

    abstract fun getContacts(): List<Contact>

    data class New(val value: Contact) : ComposeItem() {
        override fun getContacts(): List<Contact> = listOf(value)
    }

    data class Recent(val value: Conversation) : ComposeItem() {
        override fun getContacts(): List<Contact> = value.recipients.map { recipient ->
            recipient.contact ?: Contact(numbers = RealmList(PhoneNumber(address = recipient.address)))
        }
    }

    data class Starred(val value: Contact) : ComposeItem() {
        override fun getContacts(): List<Contact> = listOf(value)
    }

    data class Group(val value: ContactGroup) : ComposeItem() {
        override fun getContacts(): List<Contact> = value.contacts
    }

    data class Person(val value: Contact) : ComposeItem() {
        override fun getContacts(): List<Contact> = listOf(value)
    }
}

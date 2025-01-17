
package com.messages.readmms.readsmss.feature.contacts

import com.messages.readmms.readsmss.feature.compose.editing.ComposeItem
import com.messages.readmms.readsmss.model.Contact

data class ContactsState(
    val query: String = "",
    val composeItems: List<ComposeItem> = ArrayList(),
    val selectedContact: Contact? = null // For phone number picker
)

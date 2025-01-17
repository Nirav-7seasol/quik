
package com.messages.readmms.readsmss.filter

import com.messages.readmms.readsmss.extensions.removeAccents
import com.messages.readmms.readsmss.model.ContactGroup
import javax.inject.Inject

class ContactGroupFilter @Inject constructor(private val contactFilter: ContactFilter) : Filter<ContactGroup>() {

    override fun filter(item: ContactGroup, query: CharSequence): Boolean {
        return item.title.removeAccents().contains(query, true) || // Name
                item.contacts.any { contact -> contactFilter.filter(contact, query) } // Contacts
    }

}

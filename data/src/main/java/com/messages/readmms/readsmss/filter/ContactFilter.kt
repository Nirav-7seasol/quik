
package com.messages.readmms.readsmss.filter

import com.messages.readmms.readsmss.extensions.removeAccents
import com.messages.readmms.readsmss.model.Contact
import javax.inject.Inject

class ContactFilter @Inject constructor(private val phoneNumberFilter: PhoneNumberFilter) : Filter<Contact>() {

    override fun filter(item: Contact, query: CharSequence): Boolean {
        return item.name.removeAccents().contains(query, true) || // Name
                item.numbers.map { it.address }.any { address -> phoneNumberFilter.filter(address, query) } // Number
    }

}


package com.messages.readmms.readsmss.filter

import com.messages.readmms.readsmss.model.Recipient
import javax.inject.Inject

class RecipientFilter @Inject constructor(
    private val contactFilter: ContactFilter,
    private val phoneNumberFilter: PhoneNumberFilter
) : Filter<Recipient>() {

    override fun filter(item: Recipient, query: CharSequence) = when {
        item.contact?.let { contactFilter.filter(it, query) } == true -> true
        phoneNumberFilter.filter(item.address, query) -> true
        else -> false
    }

}
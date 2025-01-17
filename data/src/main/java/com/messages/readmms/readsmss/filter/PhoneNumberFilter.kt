
package com.messages.readmms.readsmss.filter

import com.messages.readmms.readsmss.util.PhoneNumberUtils
import javax.inject.Inject

class PhoneNumberFilter @Inject constructor(
    private val phoneNumberUtils: PhoneNumberUtils
) : Filter<String>() {

    override fun filter(item: String, query: CharSequence): Boolean {
        val allCharactersDialable = query.all { phoneNumberUtils.isReallyDialable(it) }
        return allCharactersDialable && (phoneNumberUtils.compare(item, query.toString()) ||
                phoneNumberUtils.normalizeNumber(item).contains(phoneNumberUtils.normalizeNumber(query.toString())))
    }

}
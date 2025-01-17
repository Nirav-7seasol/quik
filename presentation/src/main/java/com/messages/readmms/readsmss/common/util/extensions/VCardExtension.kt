
package com.messages.readmms.readsmss.common.util.extensions

import ezvcard.VCard

fun VCard.getDisplayName(): String? {
    return formattedName?.value
            ?: telephoneNumbers?.firstOrNull()?.text
            ?: emails?.firstOrNull()?.value
}

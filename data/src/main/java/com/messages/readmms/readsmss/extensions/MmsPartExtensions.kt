
package com.messages.readmms.readsmss.extensions

import com.google.android.mms.ContentType
import com.messages.readmms.readsmss.model.MmsPart

fun MmsPart.isSmil() = ContentType.APP_SMIL == type

fun MmsPart.isImage() = ContentType.isImageType(type)

fun MmsPart.isVideo() = ContentType.isVideoType(type)

fun MmsPart.isText() = ContentType.TEXT_PLAIN == type

fun MmsPart.isVCard() = ContentType.TEXT_VCARD == type

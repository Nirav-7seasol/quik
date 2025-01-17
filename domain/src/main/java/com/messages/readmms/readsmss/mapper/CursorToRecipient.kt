
package com.messages.readmms.readsmss.mapper

import android.database.Cursor
import com.messages.readmms.readsmss.model.Recipient

interface CursorToRecipient : Mapper<Cursor, Recipient> {

    fun getRecipientCursor(): Cursor?

    fun getRecipientCursor(id: Long): Cursor?

}
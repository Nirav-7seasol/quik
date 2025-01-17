
package com.messages.readmms.readsmss.mapper

import android.database.Cursor
import com.messages.readmms.readsmss.model.ContactGroup

interface CursorToContactGroup : Mapper<Cursor, ContactGroup> {

    fun getContactGroupsCursor(): Cursor?

}

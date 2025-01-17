
package com.messages.readmms.readsmss.mapper

import android.database.Cursor
import com.messages.readmms.readsmss.model.Contact

interface CursorToContact : Mapper<Cursor, Contact> {

    fun getContactsCursor(): Cursor?

}
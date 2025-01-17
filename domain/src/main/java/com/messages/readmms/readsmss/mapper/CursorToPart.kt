
package com.messages.readmms.readsmss.mapper

import android.database.Cursor
import com.messages.readmms.readsmss.model.MmsPart

interface CursorToPart : Mapper<Cursor, MmsPart> {

    fun getPartsCursor(messageId: Long? = null): Cursor?

}

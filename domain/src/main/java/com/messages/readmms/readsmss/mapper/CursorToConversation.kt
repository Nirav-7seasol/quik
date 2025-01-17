
package com.messages.readmms.readsmss.mapper

import android.database.Cursor
import com.messages.readmms.readsmss.model.Conversation

interface CursorToConversation : Mapper<Cursor, Conversation> {

    fun getConversationsCursor(): Cursor?

}
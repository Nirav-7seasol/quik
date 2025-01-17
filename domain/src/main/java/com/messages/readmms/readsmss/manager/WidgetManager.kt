
package com.messages.readmms.readsmss.manager

interface WidgetManager {

    companion object {
        const val ACTION_NOTIFY_DATASET_CHANGED = "com.messages.readmms.readsmss.intent.action.ACTION_NOTIFY_DATASET_CHANGED"
    }

    fun updateUnreadCount()

    fun updateTheme()

}
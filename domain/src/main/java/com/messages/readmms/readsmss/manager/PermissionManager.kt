
package com.messages.readmms.readsmss.manager

interface PermissionManager {

    fun isDefaultSms(): Boolean

    fun hasReadSms(): Boolean

    fun hasSendSms(): Boolean

    fun hasContacts(): Boolean

    fun hasNotifications(): Boolean

    fun hasPhone(): Boolean

    fun hasCalling(): Boolean

    fun hasStorage(): Boolean

    fun hasExactAlarms(): Boolean

}

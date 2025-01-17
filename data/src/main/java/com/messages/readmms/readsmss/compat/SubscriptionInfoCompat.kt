
package com.messages.readmms.readsmss.compat

import android.annotation.TargetApi
import android.os.Build
import android.telephony.SubscriptionInfo

data class SubscriptionInfoCompat(private val subscriptionInfo: SubscriptionInfo) {

    val subscriptionId get() = subscriptionInfo.subscriptionId

    val simSlotIndex get() = subscriptionInfo.simSlotIndex

    val displayName get() = subscriptionInfo.displayName

}

package com.messages.readmms.readsmss.compat

import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import com.messages.readmms.readsmss.manager.PermissionManager
import javax.inject.Inject

class SubscriptionManagerCompat @Inject constructor(context: Context, private val permissions: PermissionManager) {

    private val subscriptionManager: SubscriptionManager?
        get() = field?.takeIf { permissions.hasPhone() }

    val activeSubscriptionInfoList: List<SubscriptionInfoCompat>
        get() {
            return if (permissions.hasPhone()) {
                try {
                    subscriptionManager?.activeSubscriptionInfoList?.map {
                        SubscriptionInfoCompat(it)
                    } ?: listOf()
                } catch (e: SecurityException) {
                    // Handle exception if permission is not granted
                    listOf()
                }
            } else {
                listOf()
            }
        }

    init {
        subscriptionManager = SubscriptionManager.from(context)
    }

    fun addOnSubscriptionsChangedListener(listener: OnSubscriptionsChangedListener) {
        subscriptionManager?.addOnSubscriptionsChangedListener(listener.listener)
    }

    fun removeOnSubscriptionsChangedListener(listener: OnSubscriptionsChangedListener) {
        subscriptionManager?.removeOnSubscriptionsChangedListener(listener.listener)
    }

    abstract class OnSubscriptionsChangedListener {

        val listener: SubscriptionManager.OnSubscriptionsChangedListener? =
            object : SubscriptionManager.OnSubscriptionsChangedListener() {
                override fun onSubscriptionsChanged() {
                    this@OnSubscriptionsChangedListener.onSubscriptionsChanged()
                }
            }

        abstract fun onSubscriptionsChanged()

    }

}
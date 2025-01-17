package com.moez.QKSMS.manager

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dev.octoshrimpy.quik.manager.ReferralManager
import dev.octoshrimpy.quik.util.Preferences
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class ReferralManagerImpl @Inject constructor(
    private val context: Context,
    private val prefs: Preferences
) : ReferralManager {

    override suspend fun trackReferrer() {
        if (prefs.didSetReferrer.get()) {
            return
        }

        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        val responseCode = suspendCancellableCoroutine { cont ->
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    cont.resume(responseCode)
                }

                override fun onInstallReferrerServiceDisconnected() {
                    cont.resume(InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED)
                }
            })

            cont.invokeOnCancellation {
                referrerClient.endConnection()
            }
        }

        when (responseCode) {
            InstallReferrerClient.InstallReferrerResponse.OK -> {
                prefs.didSetReferrer.set(true)
            }

            InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                prefs.didSetReferrer.set(true)
            }
        }

        referrerClient.endConnection()
    }

}

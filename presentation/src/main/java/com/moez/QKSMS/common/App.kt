/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.octoshrimpy.quik.common

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.moez.QKSMS.common.SharedPrefs
import com.moez.QKSMS.myadsworld.MyAddPrefs
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass
import com.moez.QKSMS.myadsworld.MyAppOpenManager
import com.uber.rxdogtag.RxDogTag
import com.uber.rxdogtag.autodispose.AutoDisposeConfigurer
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.common.util.FileLoggingTree
import dev.octoshrimpy.quik.injection.AppComponentManager
import dev.octoshrimpy.quik.injection.appComponent
import dev.octoshrimpy.quik.manager.BillingManager
import dev.octoshrimpy.quik.manager.ReferralManager
import dev.octoshrimpy.quik.migration.QkMigration
import dev.octoshrimpy.quik.migration.QkRealmMigration
import dev.octoshrimpy.quik.util.NightModeManager
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasBroadcastReceiverInjector, HasServiceInjector {


    @Suppress("unused")
    @Inject lateinit var qkMigration: QkMigration

    @Inject lateinit var billingManager: BillingManager
    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var dispatchingBroadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>
    @Inject lateinit var fileLoggingTree: FileLoggingTree
    @Inject lateinit var nightModeManager: NightModeManager
    @Inject lateinit var realmMigration: QkRealmMigration
    @Inject lateinit var referralManager: ReferralManager

    companion object {
        var isSchedule = false
        lateinit var ctx: App

        fun isConnected(context: Context): kotlin.Boolean {
            return try {
                val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val nInfo = cm.activeNetworkInfo
                nInfo != null && nInfo.isAvailable && nInfo.isConnected
            } catch (e: Exception) {
                Log.e("Connectivity Exception", e.message!!)
                false
            }
        }
    }

    var myAddPrefs: MyAddPrefs? = null

    override fun onCreate() {
        super.onCreate()
        ctx = this
        SharedPrefs.init(this)
        AppComponentManager.init(this)
        appComponent.inject(this)
        myAddPrefs = MyAddPrefs(this)
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .compactOnLaunch()
                .migration(realmMigration)
                .schemaVersion(QkRealmMigration.SchemaVersion)
                .build())

        qkMigration.performMigration()

        GlobalScope.launch(Dispatchers.IO) {
            referralManager.trackReferrer()
        }

        nightModeManager.updateCurrentTheme()

        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs)

        EmojiCompat.init(FontRequestEmojiCompatConfig(this, fontRequest))

        RxDogTag.builder()
                .configureWith(AutoDisposeConfigurer::configure)
                .install()
        getData(this)
    }

    fun getData(context: Context?) {
//        Log.d("TAG", "getDatassss: " + MyAESUTIL.decrypt(MyAllAdCommonClass.JSON_URL))

        val requestQueue = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(
            Request.Method.GET,
            MyAllAdCommonClass.JSON_URL,
            null,
            { response ->
                try {
                    val tutorialsObject = JSONObject(response.toString())
                    myAddPrefs?.admNativeId = tutorialsObject.getString("nativeId")
                    myAddPrefs?.admInterId = tutorialsObject.getString("interstialId")
                    myAddPrefs?.admBannerId = tutorialsObject.getString("bannerId")
                    myAddPrefs?.admAppOpenId = tutorialsObject.getString("appopenId")
                    myAddPrefs!!.buttonColor = tutorialsObject.getString("addButtonColor")

//                    val tutorialsObject2 = tutorialsObject.getJSONObject("extraFields")
//                    ABAddPrefs?.setSplashInterAppOpen(
//                        tutorialsObject2.getString("splash_inter_appopen").toInt()
//                    )

                    // Load an ad.
//                        loadAd();
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("admdfdjkdfj22", "onResponse: " + e.message)
                }
            }) { error -> Log.e("resposnscenotnull", "onErrorResponse: $error") }
        requestQueue.add(req)
    }

    fun loadAppOpen() {
       MyAppOpenManager(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return dispatchingBroadcastReceiverInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }

}
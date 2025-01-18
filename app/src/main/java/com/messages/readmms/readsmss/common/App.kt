package com.messages.readmms.readsmss.common

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
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.util.FileLoggingTree
import com.messages.readmms.readsmss.injection.AppComponentManager
import com.messages.readmms.readsmss.injection.appComponent
import com.messages.readmms.readsmss.manager.BillingManager
import com.messages.readmms.readsmss.manager.ReferralManager
import com.messages.readmms.readsmss.migration.QkMigration
import com.messages.readmms.readsmss.migration.QkRealmMigration
import com.messages.readmms.readsmss.myadsworld.MyAddPrefs
import com.messages.readmms.readsmss.myadsworld.MyAllAdCommonClass
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager
import com.messages.readmms.readsmss.util.NightModeManager
import com.uber.rxdogtag.RxDogTag
import com.uber.rxdogtag.autodispose.AutoDisposeConfigurer
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
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
    @Inject
    lateinit var qkMigration: QkMigration

    @Inject
    lateinit var billingManager: BillingManager

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingBroadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var fileLoggingTree: FileLoggingTree

    @Inject
    lateinit var nightModeManager: NightModeManager

    @Inject
    lateinit var realmMigration: QkRealmMigration

    @Inject
    lateinit var referralManager: ReferralManager

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

        var adLoaded: () -> Unit = {}
        var adFailed: () -> Unit = {}
    }

    var myAddPrefs: MyAddPrefs? = null

    private val appOpenAdListener = object : AppOpenAdListener {
        override fun appOpenLoaded() {
            adLoaded()
        }

        override fun appOpenFailed() {
            adFailed()
        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        ctx = this
        SharedPrefs.init(this)
        AppComponentManager.init(this)
        appComponent.inject(this)
        myAddPrefs = MyAddPrefs(this)
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .compactOnLaunch()
                .migration(realmMigration)
                .schemaVersion(QkRealmMigration.SchemaVersion)
                .build()
        )

        qkMigration.performMigration()

        GlobalScope.launch(Dispatchers.IO) {
            referralManager.trackReferrer()
        }

        nightModeManager.updateCurrentTheme()

        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )

        EmojiCompat.init(FontRequestEmojiCompatConfig(this, fontRequest))

        RxDogTag.builder()
            .configureWith(AutoDisposeConfigurer::configure)
            .install()
        getData(this)
    }

    fun getData(context: Context?) {
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
                    loadAppOpen(appOpenAdListener)
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

    fun loadAppOpen(appOpenAdListener: AppOpenAdListener) {
        Log.e("TAG1212", "loadAppOpen: ")
        MyAppOpenManager(this, appOpenAdListener)
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

interface AppOpenAdListener {
    fun appOpenLoaded()
    fun appOpenFailed()
}

interface AppOpenCloseListener {
    fun adClosed()
}
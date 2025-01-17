package com.messages.readmms.readsmss.feature.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.FormError
import com.messages.readmms.readsmss.feature.permission.PermissionActivity
import com.messages.readmms.readsmss.common.SharedPrefs
import com.messages.readmms.readsmss.myadsworld.MyAddPrefs
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager
import com.messages.readmms.readsmss.myadsworld.MyGoogleMobileAdsConsentManager
import com.messages.readmms.readsmss.myadsworld.MySplashAppOpenAds
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.feature.main.MainActivity
import java.util.concurrent.atomic.AtomicBoolean

class MySplashActivity : AppCompatActivity() {

    var myApplication: App? = null
    var myGoogleMobileAdsConsentManager: MyGoogleMobileAdsConsentManager? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    var myAddPrefs: MyAddPrefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            installSplashScreen()
                .setKeepOnScreenCondition { true }
        } else {
            setContentView(R.layout.activity_splash_my)
        }

//        myAddPrefs = MyAddPrefs(this)
//        myApplication = App()
//        MyAppOpenManager.appOpenAd = null
//
//        myGoogleMobileAdsConsentManager =
//            MyGoogleMobileAdsConsentManager.getInstance(applicationContext)
//        myGoogleMobileAdsConsentManager?.gatherConsent(
//            this
//        ) { consentError: FormError? ->
//            if (consentError != null) {
//                // Consent not obtained in current session.
//                Log.w(
//                    "GoogleConsentError",
//                    String.format(
//                        "%s: %s",
//                        consentError.errorCode,
//                        consentError.message
//                    )
//                )
//            }
//            //                        Log.e("fgfgfffgffg", "onCreate: "+AESUTIL.decrypt(AllAdCommonClass.JSON_URL) );
//            if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
//                initializeMobileAdsSdk()
//                GoNextScren()
//            } else {
//                GoNextScren()
//            }
//        }
//
//        // This sample attempts to load ads using consent obtained in the previous session.
//        if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
//            initializeMobileAdsSdk()
//        }
        GoNextScren()
    }

    fun GoNextScren() {
        if (SharedPrefs.isInitialLanguageSet) {
            if (!Settings.canDrawOverlays(this@MySplashActivity)) {
                val intent = Intent(
                    this@MySplashActivity,
                    PermissionActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@MySplashActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        } else {
            MyAppOpenManager.Strcheckad = "StrClosed"
            MySplashAppOpenAds.SplashAppOpenShow(this@MySplashActivity)
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(
            this
        ) { // Load an ad.
            //                        loadAd();
            App.ctx.loadAppOpen()
        }
    }
}

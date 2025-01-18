package com.messages.readmms.readsmss.feature.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.messages.readmms.readsmss.R
import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.common.AppOpenAdListener
import com.messages.readmms.readsmss.common.AppOpenCloseListener
import com.messages.readmms.readsmss.common.SharedPrefs
import com.messages.readmms.readsmss.feature.language.LanguageSelectionActivity
import com.messages.readmms.readsmss.feature.main.MainActivity
import com.messages.readmms.readsmss.feature.permission.PermissionActivity
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager
import java.util.concurrent.atomic.AtomicBoolean

class MySplashActivity : com.messages.readmms.readsmss.callendservice.BaseActivity() {

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private val splashDuration: Long = 5000 // 3 seconds
    private var isAdLoaded = false
    private val handler = Handler(Looper.getMainLooper())
    private var isAdLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharedPrefs.isFirstAppOpenPending) {
            initializeMobileAdsSdk()
            setContentView(R.layout.activity_splash_my)
            handler.postDelayed({
                if (!isAdLoaded) {
                    navigateToNextScreen()
                }
            }, splashDuration)
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                installSplashScreen()
                    .setKeepOnScreenCondition { true }
            } else {
                setContentView(R.layout.activity_splash_my)
            }
            navigateToNextScreen()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        MobileAds.initialize(
            this
        ) {
            if (isAdLoading) {
                return@initialize
            }
            isAdLoading = true
            App.adLoaded = {
                isAdLoaded = true
                handler.removeCallbacksAndMessages(null)
                MyAppOpenManager.showAd(object : AppOpenCloseListener {
                    override fun adClosed() {
                        navigateToNextScreen()
                    }
                })
                SharedPrefs.isFirstAppOpenPending = false
                MyAppOpenManager.appOpenAdListener = null
            }
            App.adFailed = {
                handler.removeCallbacksAndMessages(null)
                navigateToNextScreen()
                SharedPrefs.isFirstAppOpenPending = false
                MyAppOpenManager.appOpenAdListener = null
            }
        }
    }

    private fun navigateToNextScreen() {
        MyAppOpenManager.appOpenAdListener = null
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
            val intent = Intent(
                this@MySplashActivity,
                LanguageSelectionActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

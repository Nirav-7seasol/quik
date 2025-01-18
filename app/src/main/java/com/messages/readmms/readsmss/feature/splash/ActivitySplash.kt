package com.messages.readmms.readsmss.feature.splash

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import com.messages.readmms.readsmss.common.App.Companion.ctx
import com.messages.readmms.readsmss.common.SharedPrefs
import com.messages.readmms.readsmss.databinding.ActivitySplashMyBinding
import com.messages.readmms.readsmss.feature.language.LanguageSelectionActivity
import com.messages.readmms.readsmss.feature.main.MainActivity
import com.messages.readmms.readsmss.feature.permission.PermissionActivity
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager

import java.util.concurrent.atomic.AtomicBoolean


class ActivitySplash : com.messages.readmms.readsmss.callendservice.BaseActivity() {

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    lateinit var binding: ActivitySplashMyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            binding = ActivitySplashMyBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } else {
            this.installSplashScreen().setKeepOnScreenCondition { false }
        }

        // Determine whether to show the ad or navigate directly
        val hasShownAd = SharedPrefs.isFirstAppOpenPending.not()

        if (!hasShownAd) {
            // Show the open ad for the first launch
            showOpenAdAndNavigate()
        } else {
            // Skip the ad and navigate directly to the main activity
            navigateToMainActivity()
        }
    }

    private fun showOpenAdAndNavigate() {
        binding = ActivitySplashMyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeMobileAdsSdk()

        MyAppOpenManager.Strcheckad = "StrClosed"

        Handler(Looper.getMainLooper()).postDelayed({
            MySplashAppOpenAds.SplashAppOpenShow(this@ActivitySplash) {
                SharedPrefs.isFirstAppOpenPending = false
                navigateToMainActivity()
            }
        }, 2500)

    }

    private fun navigateToMainActivity() {
        val preferences: SharedPreferences =
            getSharedPreferences("onBoardingScreen", Activity.MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean("language_first", true)
        if (SharedPrefs.isInitialLanguageSet) {
            if (!Settings.canDrawOverlays(this@ActivitySplash)) {
                val intent = Intent(
                    this@ActivitySplash,
                    PermissionActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(
                    this@ActivitySplash,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        } else {
            val intent = Intent(
                this@ActivitySplash,
                LanguageSelectionActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        MobileAds.initialize(this) {
            Log.e("TAGed", "initializeMobileAdsSdk: ")
            ctx.getData(this@ActivitySplash)
        }
    }
}


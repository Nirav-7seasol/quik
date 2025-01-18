package com.messages.readmms.readsmss.feature.language

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.FormError
import com.messages.readmms.readsmss.common.App
import com.messages.readmms.readsmss.common.SharedPrefs
import com.messages.readmms.readsmss.feature.permission.PermissionActivity
import com.messages.readmms.readsmss.myadsworld.MyAddPrefs
import com.messages.readmms.readsmss.myadsworld.MyAllAdCommonClass.SmallNativeBannerLoad
import dagger.android.AndroidInjection
import com.messages.readmms.readsmss.common.base.QkThemedActivity
import com.messages.readmms.readsmss.common.util.extensions.viewBinding
import com.messages.readmms.readsmss.databinding.ActivityLanguageSelectionBinding
import com.messages.readmms.readsmss.feature.main.MainActivity
import com.messages.readmms.readsmss.myadsworld.MyAllAdCommonClass
import com.messages.readmms.readsmss.myadsworld.MyAppOpenManager
import com.messages.readmms.readsmss.myadsworld.MyGoogleMobileAdsConsentManager
import com.messages.readmms.readsmss.myadsworld.MySplashAppOpenAds
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class LanguageSelectionActivity : QkThemedActivity() {

    private var myApplication: App? = null
    private var myGoogleMobileAdsConsentManager: MyGoogleMobileAdsConsentManager? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var myAddPrefs: MyAddPrefs? = null

    private var languageSelectionAdapter: LanguageSelectionAdapter? = null

    private val binding by viewBinding(ActivityLanguageSelectionBinding::inflate)

    private val languagesList = mutableListOf(
        Language.ENGLISH,
        Language.HINDI,
        Language.ARABIC,
        Language.CZECH,
        Language.DANISH,
        Language.GERMAN,
        Language.SPANISH,
        Language.FRENCH,
        Language.ITALIAN,
        Language.JAPANESE,
        Language.KOREAN,
        Language.DUTCH,
        Language.POLISH,
        Language.PORTUGUESE,
        Language.RUSSIAN,
        Language.SWEDISH,
        Language.TURKISH,
        Language.UKRAINIAN,
        Language.VIETNAMESE
    )

    private val isFromSettings: Boolean by lazy { intent.getBooleanExtra("FROM_SETTING", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        myAddPrefs = MyAddPrefs(this)
        myApplication = App()
        MyAppOpenManager.appOpenAd = null

        myGoogleMobileAdsConsentManager =
            MyGoogleMobileAdsConsentManager.getInstance(applicationContext)
        myGoogleMobileAdsConsentManager?.gatherConsent(
            this
        ) { consentError: FormError? ->
            if (consentError != null) {
                // Consent not obtained in current session.
                Log.w(
                    "GoogleConsentError",
                    String.format(
                        "%s: %s",
                        consentError.errorCode,
                        consentError.message
                    )
                )
            }
            //                        Log.e("fgfgfffgffg", "onCreate: "+AESUTIL.decrypt(AllAdCommonClass.JSON_URL) );
            if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
                initializeMobileAdsSdk()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (myGoogleMobileAdsConsentManager?.canRequestAds() == true) {
            initializeMobileAdsSdk()
        }

        if (SharedPrefs.isInitialLanguageSet && isFromSettings.not()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setListeners()
        bindMethods()
        if (isFromSettings) {
            showBackButton(true)
        }

        SmallNativeBannerLoad(
            this,
            binding.myTemplate,
            binding.shimmerViewContainer,
            MyAddPrefs(this).admNativeId,
            colors.theme().theme
        )
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

    private fun bindMethods() {
        initAdapter()
    }

    private fun initAdapter() {
        languageSelectionAdapter = LanguageSelectionAdapter(
            languageList = languagesList,
            theme = colors.theme()
        )
        binding.rcvLanguages.layoutManager = GridLayoutManager(this, 1)
        binding.rcvLanguages.adapter = languageSelectionAdapter
        binding.rcvLanguages.addItemDecoration(ItemMarginDecoration(dpToPx(10), dpToPx(5)))
    }

    private fun setListeners() {
        binding.btnNext.setOnClickListener {
            languageSelectionAdapter?.getSelectedLanguage()?.let { language ->
                SharedPrefs.selectedLanguage = language
                setLanguage(Locale.forLanguageTag(language.code))
                SharedPrefs.isInitialLanguageSet = true
                if (isFromSettings) {
                    finish()
                } else {
                    if (!Settings.canDrawOverlays(this)) {
                        val intent = Intent(this, PermissionActivity::class.java)
                        if (MyAppOpenManager.isAdAvailable() && SharedPrefs.isFirstAppOpenPending) {
                            MyAppOpenManager.Strcheckad = "StrClosed"
                            MySplashAppOpenAds.SplashAppOpenShow(
                                this@LanguageSelectionActivity,
                                intent
                            )
                        } else {
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
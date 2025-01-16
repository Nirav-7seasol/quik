package com.moez.QKSMS.feature.language

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.recyclerview.widget.GridLayoutManager
import com.moez.QKSMS.common.SharedPrefs
import com.moez.QKSMS.myadsworld.MyAddPrefs
import com.moez.QKSMS.myadsworld.MyAllAdCommonClass.SmallNativeBannerLoad
import dagger.android.AndroidInjection
import dev.octoshrimpy.quik.common.base.QkThemedActivity
import dev.octoshrimpy.quik.common.util.extensions.viewBinding
import dev.octoshrimpy.quik.databinding.ActivityLanguageSelectionBinding
import dev.octoshrimpy.quik.feature.main.MainActivity
import java.util.Locale

class LanguageSelectionActivity : QkThemedActivity() {

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
                        //todo:open permission activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
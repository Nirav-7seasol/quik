package com.moez.QKSMS.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moez.QKSMS.feature.language.Language

class SharedPrefs {

    private lateinit var context: Context

    private fun getContext() = if (::context.isInitialized) context
    else throw RuntimeException("Please Initialize SharedPrefs")

    private var initialized = false
    private var listeners: ArrayList<SharedPreferences.OnSharedPreferenceChangeListener> =
        arrayListOf()

    private val pref by lazy {
        getInstance().getContext().getSharedPreferences("Message_App", Context.MODE_PRIVATE)
    }

    private fun edit(operation: SharedPreferences.Editor.() -> Unit) {
        val editor = getInstance().pref.edit()
        operation(editor)
        editor.apply()
    }

    companion object {
        fun init(context: Context) {
            if (getInstance().initialized.not()) {
                getInstance().context = context
                getInstance().initialized = true
            } else println("Already initialized")
        }

        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPrefs? = null

        private const val IS_INITIAL_LANGUAGE_SET = "IS_INITIAL_LANGUAGE_SET"
        const val LANGUAGE = "LANGUAGE"
        const val IS_SHOW_CALLED_ID = "isSHowCallerID"

        private fun getInstance(): SharedPrefs {
            return instance ?: synchronized(this) { SharedPrefs().also { instance = it } }
        }

        fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            if (!getInstance().listeners.contains(listener)) {
                getInstance().listeners.add(listener)
                getInstance().pref.registerOnSharedPreferenceChangeListener(listener)
            }
        }

        var selectedLanguage: Language
            get() = Gson().fromJson(getInstance().pref.getString(LANGUAGE, null))
                ?: Language.ENGLISH
            set(value) = getInstance().edit { putString(LANGUAGE, Gson().toJson(value)) }

        var isInitialLanguageSet: Boolean
            get() = getInstance().pref.getBoolean(IS_INITIAL_LANGUAGE_SET, false)
            set(value) = getInstance().edit { putBoolean(IS_INITIAL_LANGUAGE_SET, value) }

        var isShowCalledId: Boolean
            get() = getInstance().pref.getBoolean(IS_SHOW_CALLED_ID, false)
            set(value) = getInstance().edit { putBoolean(IS_SHOW_CALLED_ID, value) }
    }
}
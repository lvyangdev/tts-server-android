package com.github.jing332.tts_server_android.ui

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.updatePadding
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.github.jing332.tts_server_android.BuildConfig
import com.github.jing332.tts_server_android.R
import com.github.jing332.tts_server_android.R.string
import com.github.jing332.tts_server_android.R.xml
import com.github.jing332.tts_server_android.app
import com.github.jing332.tts_server_android.constant.CodeEditorTheme
import com.github.jing332.tts_server_android.help.config.AppConfig
import com.github.jing332.tts_server_android.help.config.SysTtsConfig
import com.github.jing332.tts_server_android.util.longToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppConfig.kotprefName
        setPreferencesFromResource(xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 朗读目标多选开关
        val voiceMultipleSwitch: SwitchPreferenceCompat = findPreference("isVoiceMultiple")!!
        voiceMultipleSwitch.isChecked = SysTtsConfig.isVoiceMultipleEnabled
        voiceMultipleSwitch.setOnPreferenceChangeListener { _, newValue ->
            SysTtsConfig.isVoiceMultipleEnabled = newValue as Boolean
            true
        }

        // 分组多选开关
        val groupMultipleSwitch: SwitchPreferenceCompat = findPreference("groupMultiple")!!
        groupMultipleSwitch.isChecked = SysTtsConfig.isGroupMultipleEnabled
        groupMultipleSwitch.setOnPreferenceChangeListener { _, newValue ->
            SysTtsConfig.isGroupMultipleEnabled = newValue as Boolean
            true
        }

        // 代码编辑器主题
        val editorTheme: ListPreference = findPreference("codeEditorTheme")!!
        val themes = linkedMapOf(
            CodeEditorTheme.AUTO to requireContext().getString(R.string.app_language_follow),
            CodeEditorTheme.QUIET_LIGHT to "Quiet-Light",
            CodeEditorTheme.SOLARIZED_DRAK to "Solarized-Dark",
            CodeEditorTheme.DARCULA to "Darcula",
            CodeEditorTheme.ABYSS to "Abyss"
        )
        editorTheme.entries = themes.values.toTypedArray()
        editorTheme.entryValues = themes.keys.map { it.toString() }.toTypedArray()
        editorTheme.setValueIndex(AppConfig.codeEditorTheme)
        editorTheme.summary = themes[AppConfig.codeEditorTheme]
        editorTheme.setOnPreferenceChangeListener { _, newValue ->
            AppConfig.codeEditorTheme = newValue.toString().toInt()
            editorTheme.setValueIndex(AppConfig.codeEditorTheme)
            editorTheme.summary = themes[AppConfig.codeEditorTheme]
            true
        }

        // 语言
        val langPre: ListPreference = findPreference("language")!!

        val appLocales = BuildConfig.TRANSLATION_ARRAY.map { Locale.forLanguageTag(it) }
        val entries = appLocales.map { it.getDisplayName(it) }.toMutableList()
            .apply { add(0, getString(string.app_language_follow)) }

        langPre.entries = entries.toTypedArray()
        langPre.entryValues =
            mutableListOf("").apply { addAll(BuildConfig.TRANSLATION_ARRAY) }.toTypedArray()

        val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)
        langPre.setValueIndex(
            if (currentLocale == null) 0
            else {
                val languageTag = currentLocale.toLanguageTag()
                appLocales.indexOfFirst { it.toLanguageTag() == languageTag } + 1
            }
        )
        langPre.summary =
            if (currentLocale == null) getString(R.string.app_language_follow)
            else currentLocale.getDisplayName(currentLocale)
        langPre.setDialogMessage(R.string.app_language_to_follow_tip_msg)
        langPre.setOnPreferenceChangeListener { _, newValue ->
            val locale = if (newValue == null || newValue.toString().isEmpty()) { //随系统
                longToast(string.app_language_to_follow_tip_msg)
                app.updateLocale(Locale.getDefault())
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.create(Locale.forLanguageTag(newValue.toString()))
            }
            AppCompatDelegate.setApplicationLocales(locale)

            true
        }

    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is ListPreference) {
            val tvMsg = TextView(requireContext()).apply {
                setTypeface(null, Typeface.BOLD)
                text = preference.dialogMessage
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                updatePadding(left = 12, right = 12, top = 20)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(preference.dialogTitle)
                .setView(tvMsg)
                .setSingleChoiceItems(
                    preference.entries,
                    preference.findIndexOfValue(preference.value)
                ) { dlg, which ->
                    preference.callChangeListener(preference.entryValues[which].toString())
                    dlg.dismiss()
                }
                .setPositiveButton(android.R.string.cancel) { _, _ -> }
                .show()

        } else
            super.onDisplayPreferenceDialog(preference)
    }
}
package es.michaelcharl.pokejisho.ui.state

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    EN("en"),
    JA("ja"),
}

/** Wraps the AppCompat per-app locale API (auto-persisted via the manifest service). */
object LocalizationManager {
    fun current(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .substringBefore("-")
            .lowercase()
        return when (tag) {
            "en" -> AppLanguage.EN
            "ja" -> AppLanguage.JA
            else -> AppLanguage.SYSTEM
        }
    }

    fun set(language: AppLanguage) {
        val locales = language.tag
            ?.let { LocaleListCompat.forLanguageTags(it) }
            ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }
}

package com.agrisurvey.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtil {
    private const val LANGUAGE_KEY = "app_language"

    fun setLocale(context: Context, language: String): Context {
        persistLanguage(context, language)
        return updateResources(context, language)
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(context: Context, language: String) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_KEY, language)
            .commit()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
package com.agrisurvey.app

import android.app.Application
import android.content.Context
import com.agrisurvey.app.utils.LocaleUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgriSurveyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        val language = LocaleUtil.getCurrentLanguage(base)
        super.attachBaseContext(LocaleUtil.setLocale(base, language))
    }
}
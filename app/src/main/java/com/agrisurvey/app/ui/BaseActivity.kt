package com.agrisurvey.app.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.agrisurvey.app.utils.LocaleUtil

//class BaseActivity {
//}

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val context = LocaleUtil.setLocale(newBase, LocaleUtil.getCurrentLanguage(newBase))
        super.attachBaseContext(context)
    }
}
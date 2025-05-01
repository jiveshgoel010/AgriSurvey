package com.agrisurvey.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agrisurvey.app.R
import com.agrisurvey.app.ui.auth.PhoneAuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 2000L // 2 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        setTheme(R.style.Theme_AgriSurvey)


        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoImageView.startAnimation(fadeInAnimation)

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashDelay)
            startActivity(Intent(this@SplashActivity, PhoneAuthActivity::class.java))
            finish()
        }
    }
}
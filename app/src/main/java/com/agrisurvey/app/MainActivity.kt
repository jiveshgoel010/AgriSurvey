package com.agrisurvey.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.agrisurvey.app.databinding.ActivityMainBinding
import com.agrisurvey.app.ui.BaseActivity
import com.agrisurvey.app.ui.auth.PhoneAuthActivity
import com.agrisurvey.app.ui.home.HomeFragment
import com.agrisurvey.app.ui.insights.InsightsNavigationFragment
import com.agrisurvey.app.ui.profile.ProfileFragment
import com.agrisurvey.app.ui.survey.SurveyFragment
import com.agrisurvey.app.utils.LocaleUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
//class MainActivity : AppCompatActivity() {
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionTranslate -> {
//                    Toast.makeText(this, "Translate clicked", Toast.LENGTH_SHORT).show()

                    val currentLang = LocaleUtil.getCurrentLanguage(this)
                    val newLang = if (currentLang == "en") "hi" else "en"

                    LocaleUtil.setLocale(this, newLang)

                    // Recreate the activity to apply new locale
                    recreate()

                    true
                }

                R.id.actionLogout -> {
                    auth.signOut()
                    startActivity(Intent(this, PhoneAuthActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }

        //default
        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    loadFragment(HomeFragment())
                    setAppbar(getString(R.string.app_name), showBackButton = false)
                }

                R.id.menuSurvey -> {
                    loadFragment(SurveyFragment())
                    setAppbar(getString(R.string.title_survey), showBackButton = false)
                }

                R.id.menuInsights -> {
                    loadFragment(InsightsNavigationFragment())
                    setAppbar(getString(R.string.title_insights), showBackButton = false)
                }

                R.id.menuProfile -> {
                    loadFragment(ProfileFragment())
                    setAppbar(getString(R.string.title_profile), showBackButton = false)
                }
            }
            true
        }
    }

    fun setBottomNavItem(itemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = itemId
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun setAppbar(title: String, showBackButton: Boolean = false) {
        binding.topAppBar.title = title
        binding.topAppBar.navigationIcon = if (showBackButton) {
            ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24)
        } else null

        binding.topAppBar.setNavigationOnClickListener(
            if (showBackButton) {
                View.OnClickListener { onBackPressedDispatcher.onBackPressed() }
            } else null
        )
    }
}
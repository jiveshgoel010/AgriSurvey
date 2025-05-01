package com.agrisurvey.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.R
import com.agrisurvey.app.databinding.ActivityOtpVerifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class OtpVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        binding.textPhoneNumber.text = "$phoneNumber"
        binding.textPhoneNumber.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))


        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.editOtp.text.toString().trim()
            if (otp.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkUserProfileAndRedirect() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Profile exists -> go to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Profile doesn't exist -> go to ProfileSetupActivity
                    startActivity(Intent(this, ProfileSetupActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to check profile.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, ProfileSetupActivity::class.java))
//                    finish()
                    checkUserProfileAndRedirect()

                } else {
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
package com.agrisurvey.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.lifecycle.lifecycleScope
import com.agrisurvey.app.BuildConfig
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.databinding.ActivityPhoneAuthBinding
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var credentialManager: CredentialManager
    private lateinit var phoneNumber: String
    private val defaultWebClientId = BuildConfig.DEFAULT_WEB_CLIENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        //OTP login
        binding.btnSendOtp.setOnClickListener {
            phoneNumber = binding.editPhone.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                startPhoneNumberVerification("+91$phoneNumber")
            }
        }

        //Google login
        binding.btnGoogleSignIn.setOnClickListener {
            launchCredentialManager()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun launchCredentialManager() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(defaultWebClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {

                val result = credentialManager.getCredential(
                    context = this@PhoneAuthActivity,
                    request = request
                )
                handleSignIn(result.credential)

            } catch (e: Exception) {
                Log.e(TAG, "Couldn't retrieve user's credentials: ${e.message}")
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

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val user = FirebaseAuth.getInstance().currentUser

                    // Create a map of user data to store in Firestore (if not present)
                    val uid = user?.uid ?: return@addOnCompleteListener
                    val name = user.displayName
                    val email = user.email
                    val phone = user.phoneNumber

                    // Create a user data map for Firestore
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "phone" to phone
                    )

                    // Save user data to Firestore (if not already saved)
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                // User doesn't exist in Firestore -> Save their info
                                db.collection("users").document(uid)
                                    .set(userData, SetOptions.merge())
                                    .addOnSuccessListener {
                                        // Redirect to the MainActivity or wherever needed after the data is saved
                                        Toast.makeText(
                                            this,
                                            "User profile saved.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        checkUserProfileAndRedirect()  // Check if the profile is saved, then redirect
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this,
                                            "Error saving user profile: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                // User profile exists in Firestore
                                checkUserProfileAndRedirect()  // Just redirect to MainActivity
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Error checking user profile: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // Sign-in failed
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Google sign-in failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto verification done, you could sign in directly
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(
                this@PhoneAuthActivity,
                "Verification Failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@PhoneAuthActivity.verificationId = verificationId
            val intent = Intent(this@PhoneAuthActivity, OtpVerifyActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("phoneNumber", "+91$phoneNumber")
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
    }
}
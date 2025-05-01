package com.agrisurvey.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agrisurvey.app.MainActivity
import com.agrisurvey.app.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Name and email are required.", Toast.LENGTH_SHORT).show()
            } else {
                saveUserProfile(name, email)
            }
        }
    }

    private fun saveUserProfile(name: String, email: String) {
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "uid" to uid
        )

        // Save data to Firestore under the 'users' collection
        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                // After saving, navigate to the main screen or show success message
                Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                finish()  // or navigate to the next screen
                startActivity(Intent(this, MainActivity::class.java))

            }
            .addOnFailureListener {
                Toast.makeText(this, "Error updating profile.", Toast.LENGTH_SHORT).show()
            }
    }
}
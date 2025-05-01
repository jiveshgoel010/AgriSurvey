package com.agrisurvey.app.data.repository

import android.util.Log
import com.agrisurvey.app.data.model.Survey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class SurveyRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val surveyCollection = firestore.collection("surveys")

    suspend fun submitSurvey(survey: Survey) : Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val updatedSurvey = survey.copy(userId = userId)
            val documentId = survey.timestamp.toString() + "_" + userId

            surveyCollection.document(documentId)
                .set(updatedSurvey, SetOptions.merge())
                .await()

            true
        } catch (e : Exception) {
            Log.e("SurveyRepository", "Error submitting survey: ${e.message}")
            false
        }
    }

}
package com.agrisurvey.app.data.repository

import com.agrisurvey.app.data.model.Survey
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore


class InsightsRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val surveyCollection = firestore.collection("surveys")

    fun getSurveysByState(
        state: String,
        onSuccess: (List<Survey>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        surveyCollection
            .whereEqualTo("state", state)
            .get()
            .addOnSuccessListener { result ->
                val surveys = result.documents.mapNotNull { it.toObject(Survey::class.java) }
                onSuccess(surveys)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
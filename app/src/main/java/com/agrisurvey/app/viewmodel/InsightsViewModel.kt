package com.agrisurvey.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrisurvey.app.data.model.Survey
import com.agrisurvey.app.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {

    private val _surveys = MutableLiveData<List<Survey>>()
    val surveys: LiveData<List<Survey>> = _surveys

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchSurveysForState(state: String) {
        _loading.value = true
        _error.value = null

        insightsRepository.getSurveysByState(
            state = state,
            onSuccess = { surveyList ->
                _surveys.value = surveyList
                _loading.value = false
            },
            onFailure = { exception ->
                _error.value = exception.localizedMessage
                _loading.value = false
            }
        )
    }
}
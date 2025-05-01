package com.agrisurvey.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrisurvey.app.data.model.Survey
import com.agrisurvey.app.data.repository.SurveyRepository
import kotlinx.coroutines.launch

class SurveyViewModel : ViewModel() {

    private val repository = SurveyRepository()

    private val _submissionSuccess = MutableLiveData<Boolean>()
    val submissionSuccess : LiveData<Boolean> = _submissionSuccess

    fun submitSurvey(survey: Survey) {
        viewModelScope.launch {
            val result = repository.submitSurvey(survey)
            _submissionSuccess.value = result
        }
    }
}
package com.agrisurvey.app.data.model

data class Survey(
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val landSize: String = "",             // in acres
    val irrigationType: String = "",       // dropdown
    val season: String = "",               // dropdown
    val cropType: String = "",             // dropdown
    val cropsGrown: List<String> = emptyList(),           // free text field
    val croppingPattern: String = "",      // dropdown
    val location: String = "",             // user-typed or reverse geocoded address
    val latitude: Double? = null,          // from map / GPS
    val longitude: Double? = null,         // from map / GPS
    val state: String = "",                // dropdown
    val timestamp: Long = System.currentTimeMillis()
)

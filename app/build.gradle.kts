import com.android.build.api.dsl.Packaging
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gms.google.services)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.agrisurvey.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.agrisurvey.app"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())


        buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"${properties.getProperty("DEFAULT_WEB_CLIENT_ID")}\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${properties.getProperty("GOOGLE_MAPS_API_KEY")}\"")
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = properties.getProperty("GOOGLE_MAPS_API_KEY")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources.excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.identity.jvm)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.material.v1130alpha12)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.androidx.material3)
    implementation(libs.material.v160)
    implementation(libs.pinview)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.android.maps.utils)

}



plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.resumecreaterapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.resumecreaterapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Also add the dependency for the Google Play services library and specify its version
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // latest BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database")
    //image picker dependency https://github.com/Dhaval2404/ImagePicker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    //tooltip
    implementation("com.github.douglasjunior:android-simple-tooltip:master-SNAPSHOT") {
        isChanging = true
    }
}
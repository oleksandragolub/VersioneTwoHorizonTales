plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.example.versionetwohorizontales"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.versionetwohorizontales"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    // Firebase Bill of Materials (BOM)
    implementation(platform(libs.firebase.bom.v3270))

    // Firebase libraries
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Navigation component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Altre dipendenze
    implementation(libs.play.services.base)
    implementation(libs.commons.validator)
    implementation(libs.play.services.location)
    implementation(libs.security.crypto)
    implementation(libs.gson)
    implementation(libs.commons.io)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.room.runtime)
    implementation(libs.glide)

    // Firebase BoM (Bill of Materials)
    implementation(libs.google.firebase.database)
    implementation(libs.play.services.auth)

    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)
}

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")   // Plugin necesario para Firebase
}

android {
    namespace = "com.example.pineladrin_tfg"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pineladrin_tfg"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation("com.squareup.picasso:picasso:2.71828")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // Firebase Firestore (si quieres guardar reservas)
    implementation("com.google.firebase:firebase-firestore:24.9.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
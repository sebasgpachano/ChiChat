plugins {
    kotlin("kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.pluginNavigationSafeArgs)
    alias(libs.plugins.pluginDaggerHilt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.team2.chitchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.team2.chitchat"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Android
    implementation(libs.bundles.android)
    //Navigation
    implementation(libs.bundles.navigation)
    //Lifecycle
    implementation(libs.bundles.lifecycle)
    //Retrofit
    implementation(libs.bundles.retrofit)
    //Hilt
    implementation(libs.daggerHilt)
    kapt(libs.daggerHiltCompiler)
}

kapt {
    correctErrorTypes = true
}
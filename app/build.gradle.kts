import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    kotlin("kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.pluginNavigationSafeArgs)
    alias(libs.plugins.pluginDaggerHilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.pluginCrashlytics)
    alias(libs.plugins.pluginGoogleServices)
    alias(libs.plugins.pluginKsp)
    alias(libs.plugins.sonarQube)
}

android {
    namespace = "com.team2.chitchat"
    compileSdk = 34

    lint {
        baseline = file("lint-baseline.xml")
    }

    defaultConfig {
        applicationId = "com.team2.chitchat"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }

        getByName("debug") {
            isDebuggable = true
        }
    }

    flavorDimensions.add("version")
    productFlavors {
        create("Dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Dev-ChitChat")
            buildConfigField("String", "BASE_URL", "\"https://mock-movilidad.vass.es/chatvass/\"")
        }

        create("Pro") {
            dimension = "version"
            resValue("string", "app_name", "ChitChat")
            buildConfigField("String", "BASE_URL", "\"https://mock-movilidad.vass.es/chatvass/\"")
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
    implementation(libs.androidx.security.crypto.ktx)
    //Android
    implementation(libs.bundles.android)
    //Navigation
    implementation(libs.bundles.navigation)
    //Lifecycle
    implementation(libs.bundles.lifecycle)
    //Retrofit
    implementation(libs.bundles.retrofit)
    //Okhttp
    implementation(libs.okhttp)
    //Interceptor
    implementation(libs.interceptor)
    implementation(libs.gson)
    //Hilt
    implementation(libs.daggerHilt)
    //Rooms
    implementation(libs.bundles.room)
    //coroutines
    implementation(libs.bundles.coroutines)
    //Circle image view
    implementation(libs.circleimageview)
    // Kotlin
    implementation(libs.androidx.biometric)
    // Firebase
    implementation(libs.bundles.firebase)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Kapt
    kapt(libs.daggerHiltCompiler)
    //Ksp
    ksp(libs.androidx.room.compiler)
}

kapt {
    correctErrorTypes = true
}
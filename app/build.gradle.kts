import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kedokato_dev.houcheck"
    compileSdk = 35

    val secretsProperties =  Properties()
    val secretsFile = File(rootDir, "secrets.properties")
    if (secretsFile.exists() && secretsFile.isFile) {
        secretsProperties.load(FileInputStream(secretsFile))
    }

    defaultConfig {
        applicationId = "com.kedokato_dev.houcheck"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName =  secretsProperties.getProperty("VERSION")
        buildConfigField("String", "BASE_URL_API", secretsProperties.getProperty("BASE_URL_API"))
        buildConfigField("String", "UP_LOAD_URL_API", secretsProperties.getProperty("UP_LOAD_URL_API"))
        buildConfigField("String", "VERSION", "\"${secretsProperties.getProperty("VERSION")}\"")


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

    buildFeatures{
        buildConfig = true
        resValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Navigation
    implementation(libs.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx) // <== cái này là cái xử lý suspend
    kapt(libs.room.compiler)



    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.coroutines.adapter)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation( libs.androidx.material3 )

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // coil
    implementation(libs.coil.core)
    implementation(libs.coil.compose)


    // hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // mpandroidchart
    implementation(libs.mpandroidchart)

    // calendar
    implementation(libs.android.calendar.core)

    // animation navigate screen
    implementation(libs.accompanist.navigation.animation)

    // swipe refresh
    implementation(libs.accompanist.swiperefresh)

    implementation(libs.compose.foundation)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


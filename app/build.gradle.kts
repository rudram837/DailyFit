plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.app.gym"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.app.gym"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
//    API retrofit
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
//    gif animation
//    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
//    swipe down refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
//    shimmer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    debugImplementation(libs.leakcanary)
}
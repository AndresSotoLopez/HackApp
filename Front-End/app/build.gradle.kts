plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.app.hackapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.hackapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.android.volley:volley:1.2.1")
    implementation("com.etebarian:meow-bottom-navigation:1.2.0")
    implementation("com.github.bumptech.glide:glide:4.13.0")
    implementation("com.github.bumptech.glide:compiler:4.12.0")
}
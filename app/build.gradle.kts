plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.naojianghh.bilibili3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.naojianghh.bilibili3"
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
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.tools.core)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
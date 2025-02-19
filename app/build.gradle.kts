plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.ichef"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ichef"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dagger dependencies
    implementation("com.google.dagger:hilt-android:2.53.1")
    ksp("com.google.dagger:hilt-compiler:2.53.1")

    // For instrumentation tests
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.53.1")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.53.1")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.53.1")
    kspTest("com.google.dagger:hilt-compiler:2.53.1")

    //for property ingredients, because it is not hanndling configuration change (eg.: light/dark mode)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // for API calls
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // and for converting responses/requests
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    //mocking APIs
    implementation("co.infinum:retromock:1.1.1")

    //pull-to-refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


}
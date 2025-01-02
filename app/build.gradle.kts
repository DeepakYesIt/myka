plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.yesitlabs.mykaapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yesitlabs.mykaapp"
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

    buildFeatures {
        viewBinding = true
        dataBinding= true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
    implementation("com.google.firebase:firebase-common-ktx:21.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //viewpager
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //Retrofit for api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")

    //Dimen
    implementation("com.intuit.ssp:ssp-android:1.0.5")
    implementation("com.intuit.sdp:sdp-android:1.0.6")

    //otpView
    implementation("com.github.aabhasr1:OtpView:v1.1.2-ktx")

    //circularImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //bar chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //image picker
    implementation("com.github.Dhaval2404:ImagePicker:v2.1")

    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:glide:4.9.0")
    implementation("com.github.bumptech.glide:compiler:4.9.0")

    //google Firebase
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.google.firebase:firebase-messaging:23.2.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    //power spinner
    implementation("com.github.skydoves:powerspinner:1.2.7")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt ("com.google.dagger:hilt-android-compiler:2.47")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    kapt("androidx.lifecycle:lifecycle-compiler:2.3.1")

    // firebase crashlytics
    implementation("com.google.firebase:firebase-crashlytics:18.2.9")
    implementation("com.google.firebase:firebase-analytics:20.1.2")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.activity:activity-ktx:1.3.1")

    // progress bar circle
    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    //Stripe SDK
    implementation ("com.stripe:stripe-android:16.3.0")
    implementation ("com.github.dewinjm:monthyear-picker:1.0.2")





}
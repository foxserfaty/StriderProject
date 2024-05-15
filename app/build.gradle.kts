plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.strider"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.strider"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true;
    }
}

dependencies {
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.location)
    implementation(libs.mpandroidchart)

    testImplementation(libs.junit)
    testImplementation(libs.ext.junit)
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.mockito:mockito-android:5.0.0")
    testImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation(libs.espresso.core)
}

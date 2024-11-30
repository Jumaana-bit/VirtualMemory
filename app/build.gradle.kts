plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.virtualmemory"
    compileSdk = 34

    buildFeatures {
        buildConfig = true // Enable BuildConfig fields
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.virtualmemory"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapsApiKey = project.findProperty("MAPS_API_KEY") as String?
            ?: throw IllegalArgumentException("MAPS_API_KEY is not defined in gradle.properties")

        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Room dependencies
    implementation("androidx.room:room-runtime:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")

    // RecyclerView dependency
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    // Other dependencies
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("com.google.android.material:material:1.4.0")

    // If using data binding, ensure the required dependencies are added:
    implementation ("androidx.databinding:databinding-runtime:7.0.3")
    implementation ("com.google.android.material:material:1.9.0")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
}

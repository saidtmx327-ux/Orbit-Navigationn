plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.orbit.navigation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.orbit.navigation"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}

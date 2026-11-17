import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
val dotenv = Properties().apply {
    val file = rootProject.file(".env")
    if (file.exists()) {
        file.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine
            val separator = trimmed.indexOf('=')
            if (separator <= 0) return@forEachLine
            val key = trimmed.substring(0, separator).trim()
            var value = trimmed.substring(separator + 1).trim()
            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))
            ) {
                value = value.substring(1, value.length - 1)
            }
            setProperty(key, value)
        }
    }
}
val googleWebClientId: String = (
    localProperties.getProperty("google.webClientId")
        ?: System.getenv("GOOGLE_WEB_CLIENT_ID")
        ?: dotenv.getProperty("GOOGLE_WEB_CLIENT_ID")
        ?: ""
    ).trim()

android {
    namespace = "com.example.drivenext"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.drivenext"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Web Client ID из Google Cloud Console (OAuth 2.0 Client ID -> Web application).
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.coil)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

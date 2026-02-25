plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serial)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
android {
    namespace   = "top.authorche.authormail"
    compileSdk  = 35
    defaultConfig {
        applicationId = "top.authorche.authormail"
        minSdk     = 26
        targetSdk  = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    packaging {
        resources { excludes += setOf("META-INF/LICENSE*", "META-INF/NOTICE*", "META-INF/*.md") }
    }
}
dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.ui.fonts)
    implementation(libs.material3)
    implementation(libs.material.icons)
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.navigation.compose)
    implementation(libs.browser)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.nav)
    implementation(libs.okhttp)
    implementation(libs.okhttp.log)
    implementation(libs.serialization.json)
    implementation(libs.datastore)
    implementation(libs.coroutines)
    implementation(libs.coil)
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    debugImplementation(libs.compose.ui.tooling)
}

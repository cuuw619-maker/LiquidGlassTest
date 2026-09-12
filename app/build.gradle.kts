import org.gradle.api.tasks.Copy

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val prepareWallpaper by tasks.registering(Copy::class) {
    from(rootProject.file("Wallpaper.png"))
    into(layout.buildDirectory.dir("generated/wallpaper/res/drawable-nodpi"))
    rename { "wallpaper.png" }
}

tasks.named("preBuild").configure {
    dependsOn(prepareWallpaper)
}

android {
    namespace = "com.cuuw619.liquidglasstest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cuuw619.liquidglasstest"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].res.srcDir(
        layout.buildDirectory.dir("generated/wallpaper/res")
    )

    buildFeatures { compose = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.core:core-ktx:1.15.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

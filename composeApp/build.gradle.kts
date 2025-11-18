import org.gradle.kotlin.dsl.roborazzi
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbarins.serialization)
    alias(libs.plugins.roborazzi)
}

kotlin {
    compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }

    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.datastore)
        }

        iosMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.calf.ui)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.jetbrains.navigation.compose)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)

            @OptIn(ExperimentalComposeLibrary::class) implementation(compose.uiTest)
        }

        androidTarget {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        }

        androidInstrumentedTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class) implementation(compose.uiTest)

            implementation(libs.roborazzi.rule)
            implementation(libs.roborazzi.compose)
            implementation(libs.robolectric)
        }

        androidUnitTest.dependencies {
            implementation(kotlin("test"))

            implementation(libs.androidx.ui.test.junit4)
            implementation(libs.androidx.ui.test.manifest)

            implementation(libs.roborazzi.rule)
            implementation(libs.roborazzi.compose)
            implementation(libs.robolectric)
        }

        androidUnitTest.dependencies {
            implementation(libs.androidx.ui.test.junit4)
            implementation(libs.androidx.ui.test.manifest)

            implementation(libs.roborazzi.rule)
            implementation(libs.roborazzi.compose)
            implementation(libs.robolectric)
        }

        iosTest.dependencies { implementation(libs.roborazzi.compose.ios) }
    }
}

android {
    namespace = "com.swmansion.routinetracker"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.swmansion.routinetracker"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions.unitTests {
        all { test ->
            test.useJUnit {
                if (project.hasProperty("screenshot")) {
                    includeCategories("com.swmansion.routinetracker.utils.ScreenshotTests")
                }
            }
        }
        isIncludeAndroidResources = true
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes { getByName("release") { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    androidTestImplementation(libs.androidx.ui.test.junit4.android)

    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(compose.uiTooling)

    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room { schemaDirectory("$projectDir/schemas") }

roborazzi { outputDir.set(file("build/outputs/roborazzi")) }

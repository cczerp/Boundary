plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
    id("secant.jacoco-conventions")
}

android {
    namespace = "cash.z.ecc.sdk.ext"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    api(libs.upstream.sdk)
    api(libs.upstream.sdk.incubator)
    api(libs.upstream.bip39)
    api(libs.zip321)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlin.test)

    androidTestUtil(libs.androidx.test.services) {
        artifact {
            type = "apk"
        }
    }

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

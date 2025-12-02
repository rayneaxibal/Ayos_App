plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    alias(libs.plugins.map.secret) apply false
}

buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath("com.google.gms:google-services:4.4.2")
    }
}
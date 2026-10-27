// Reusable Kotlin source-level rules: Konsist checks run as tests, detekt rules run in the detekt pass.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(libs.konsist)
    // Provided by detekt at runtime when the rule set is loaded as a detekt plugin.
    compileOnly(libs.detekt.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.detekt.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

// Reusable Kotlin source-level rules: Konsist checks (run as tests) and detekt config fragments (run by detekt).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

// detekt 1.23 bundles its own Kotlin compiler (2.0.x), which clashes with this build's Kotlin and Konsist's.
// The fragment tests therefore run the detekt CLI in a separate JVM with this isolated classpath.
val detektCli by configurations.creating

dependencies {
    api(libs.konsist)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(kotlin("test"))
    testRuntimeOnly(libs.junit.platform.launcher)
    detektCli(libs.detekt.cli)
}

tasks.test {
    useJUnitPlatform()
    inputs.files(detektCli)
    inputs.dir("src/test/fixtures")
    jvmArgumentProviders.add(CommandLineArgumentProvider { listOf("-Ddetekt.cli.classpath=${detektCli.asPath}") })
}

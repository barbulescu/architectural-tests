import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// The Kotlin plugin compiles src/main/kotlin first (it sees the Java sources), then javac compiles src/main/java.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    // ArchUnit: analyses compiled bytecode, so it sees Java and Kotlin alike.
    testImplementation(libs.archunit)
    // ArchUnit logs through SLF4J; without a binding it prints a warning on every run.
    testRuntimeOnly(libs.slf4j.nop)
    // Konsist: analyses Kotlin source, so it sees Kotlin-only concepts (val/var, data classes, file layout).
    testImplementation(libs.konsist)
}

tasks.test {
    useJUnitPlatform()
    // Show ArchUnit's and Konsist's full violation messages on the console, not only in the HTML report.
    testLogging {
        events("failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    // The freeze store is read by the tests, so a changed baseline must re-run them.
    inputs.dir("archunit_store")
    // -D on the Gradle command line stays in the Gradle JVM; forward it so the presenter can re-baseline with:
    //   ./gradlew test -Darchunit.freeze.refreeze=true
    providers.systemProperty("archunit.freeze.refreeze").orNull?.let { systemProperty("archunit.freeze.refreeze", it) }
}

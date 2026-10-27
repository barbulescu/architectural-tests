// Reusable ArchUnit rules. ArchUnit reads bytecode, so these rules apply to Java and Kotlin projects alike.
plugins {
    `java-library`
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    // Consumers write their own ArchUnit tests on top of these rules, so ArchUnit is part of the API.
    api(libs.archunit)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

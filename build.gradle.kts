plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    group = "com.example.archtests"
    version = "0.1.0-SNAPSHOT"
}

// The skill installs rules by copying their source, so it carries a copy of them. CI fails if the copy is stale.
tasks.register<Sync>("bundleSkill") {
    group = "build"
    description = "Copies the rule sources into skills/architecture-tests/rules/."
    into("skills/architecture-tests/rules")
    from("java-arch-tests/src/main/java/com/example/archtests") { into("java") }
    // Konsist scans every .kt file in a project, and the skill folder lives inside the target project.
    from("kotlin-arch-tests/src/main/kotlin/com/example/archtests") { into("kotlin"); rename { "$it.txt" } }
    from("kotlin-arch-tests/src/main/resources/detekt") { into("detekt") }
}

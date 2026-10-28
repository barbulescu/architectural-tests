# Build setup

Versions tested with the rules: ArchUnit 1.5.1, Konsist 0.18.1, SLF4J 2.0.17, JUnit 5.14.4, detekt 1.23.8.
Use newer versions if the project already has them.

## Maven

Test dependencies (add the JUnit BOM to `<dependencyManagement>` if the project has no JUnit version management):

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit</artifactId>
    <version>1.5.1</version>
    <scope>test</scope>
</dependency>
<!-- ArchUnit logs through SLF4J; without a binding it prints a warning on every run. Skip if the project has one. -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-nop</artifactId>
    <version>2.0.17</version>
    <scope>test</scope>
</dependency>
<!-- Only with Kotlin rules -->
<dependency>
    <groupId>com.lemonappdev</groupId>
    <artifactId>konsist</artifactId>
    <version>0.18.1</version>
    <scope>test</scope>
</dependency>
```

Kotlin rules need `src/test/kotlin` to be compiled. In a mixed Java/Kotlin project the `kotlin-maven-plugin`'s
`test-compile` execution must list both test directories, and run before javac:

```xml
<execution>
    <id>test-compile</id>
    <goals><goal>test-compile</goal></goals>
    <configuration>
        <sourceDirs>
            <sourceDir>${project.basedir}/src/test/kotlin</sourceDir>
            <sourceDir>${project.basedir}/src/test/java</sourceDir>
        </sourceDirs>
    </configuration>
</execution>
```

If the project compiles Java with `maven-compiler-plugin`'s default executions, Kotlin must run first: disable
`default-compile`/`default-testCompile` (`<phase>none</phase>`) and add `java-compile`/`java-test-compile` executions
bound to `compile`/`test-compile`. The Kotlin documentation calls this "mixed Kotlin/Java compilation".

Surefire passes command-line `-D` properties to the tests, so `./mvnw test -Darchunit.freeze.refreeze=true` works
without configuration.

## Gradle (Kotlin DSL)

```kotlin
dependencies {
    testImplementation("com.tngtech.archunit:archunit:1.5.1")
    testRuntimeOnly("org.slf4j:slf4j-nop:2.0.17") // skip if the project has an SLF4J binding
    testImplementation("com.lemonappdev:konsist:0.18.1") // only with Kotlin rules
}

tasks.test {
    useJUnitPlatform()
    // Show the full violation messages on the console, not only in the HTML report.
    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    // The freeze store is read by the tests, so a changed baseline must re-run them. Only if archunit_store/ exists.
    inputs.dir("archunit_store")
    // -D on the Gradle command line stays in the Gradle JVM; forward it so ./gradlew test -Darchunit.freeze.refreeze=true works.
    providers.systemProperty("archunit.freeze.refreeze").orNull?.let { systemProperty("archunit.freeze.refreeze", it) }
}
```

Use the project's version catalog (`gradle/libs.versions.toml`) if it has one. For Groovy DSL, translate accordingly.
Kotlin rules in a Java-only Gradle project are pointless (nothing to check); do not add the Kotlin plugin for them.

### detekt (Gradle only)

```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

detekt {
    buildUponDefaultConfig = true          // keep detekt's defaults, add the project's config on top
    config.setFrom("config/detekt/detekt.yml")
}
```

detekt 1.23 runs with its own Kotlin compiler 2.0.x; that is fine for projects on newer Kotlin as long as nothing
forces the Kotlin version of detekt's own configuration. Run with `./gradlew detekt`; it is part of `check`.

# shop-gradle

The shop example as a Gradle build. See [the examples overview](../README.md) for what the tests check.

```bash
./gradlew test                                   # run every test, architecture tests included
./gradlew test -Darchunit.freeze.refreeze=true   # deliberately re-baseline archunit_store/
```

Mixed Java and Kotlin works out of the box with the Kotlin plugin. `build.gradle.kts` does three extra things
for the architecture tests:
* forwards `-Darchunit.freeze.refreeze` to the test JVM (Gradle keeps command-line `-D` to itself),
* declares `archunit_store/` as a test input, so a changed baseline re-runs the tests,
* prints full failure messages, so violations show on the console and not only in the HTML report.

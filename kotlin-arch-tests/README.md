# kotlin-arch-tests

Reusable rules for Kotlin source: Konsist rule groups in `src/main/kotlin/com/example/archtests/` (run as tests)
and detekt configuration fragments in `src/main/resources/detekt/` (run by detekt).

Projects do not depend on this module: the [`architecture-tests` skill](../skills/architecture-tests/SKILL.md)
copies the files they select. Fixtures are uncompiled sources in `src/test/fixtures/<group>/<variant>/`.
The detekt fragments are tested by running the detekt CLI in a separate JVM: detekt 1.23 bundles its own Kotlin
compiler, which would clash with this build's.

Konsist finds the calling test's name at a fixed stack depth, which a helper function shifts. Each rule therefore
works out its caller's name itself and passes it on, so `@Suppress("konsist.<test name>")` and the failure
messages refer to the project's test method.

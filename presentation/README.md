# Live demo

The demo runs against either example: [`examples/shop-maven`](../examples/shop-maven) or
[`examples/shop-gradle`](../examples/shop-gradle). `demo.sh` adds scenario files to the example you run it from
and removes them again. Pick the build tool your audience uses; the failures are identical.

## Runbook

Run everything from the example directory. For Gradle, replace `./mvnw test` with `./gradlew test`.

```bash
cd examples/shop-maven                        # or examples/shop-gradle
D=../../presentation/demo.sh

./mvnw test                                   # 1. green: rules pass on the clean code base

$D apply 01                                   # 2. web -> infrastructure shortcut (Java)
./mvnw test                                   #    ArchUnit fails, names the constructor and field
$D reset

$D apply 02                                   # 3. `var` in a domain data class (Kotlin)
./mvnw test                                   #    Konsist fails with file:line
$D reset

$D apply 03                                   # 4. use case without a test (Java)
./mvnw test                                   #    ArchUnit custom rule fails: "every use case has a test"
$D reset

$D apply 04                                   # 5. "our legacy code would fail everything"
./mvnw test                                   #    fails: legacy violations
./mvnw test -Darchunit.freeze.refreeze=true   #    record them as the baseline in archunit_store/
./mvnw test                                   #    green: the past is tolerated...
$D apply 01
./mvnw test                                   #    ...but the new shortcut still fails
$D reset
```

Each scenario has a `README.md` in `scenarios/` with the talking point.
Kotlin scenario files are stored as `*.kt.scenario` because Konsist scans every `.kt` file in the project.
`archunit_store.clean/` is the baseline `reset` copies back into the example.

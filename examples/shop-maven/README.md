# shop-maven

The shop example as a Maven build. See [the examples overview](../README.md) for what the tests check.

```bash
./mvnw test                                   # run every test, architecture tests included
./mvnw test -Darchunit.freeze.refreeze=true   # deliberately re-baseline archunit_store/
```

Mixed Java and Kotlin needs some wiring in `pom.xml`: the Kotlin plugin compiles first and sees the Java sources,
then javac runs in place of the default compiler executions. Surefire passes `-D` properties on to the tests.

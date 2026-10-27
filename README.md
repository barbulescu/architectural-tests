# Architecture tests with ArchUnit, Konsist and detekt

```
.
├── java-arch-tests        Gradle module: reusable ArchUnit rules (bytecode, so Java and Kotlin)
├── kotlin-arch-tests      Gradle module: reusable Konsist and detekt rules (Kotlin source)
├── examples
│   ├── shop-maven         example project (Java + Kotlin domain) with architecture tests, Maven build
│   └── shop-gradle        the same example, Gradle build
└── presentation           live-demo runbook, scenarios and demo.sh
```

Build the libraries with `./gradlew build`. Requires JDK 21+.
The examples are standalone projects: `cd examples/shop-maven && ./mvnw test`, or `cd examples/shop-gradle && ./gradlew test`.

# Rule catalog

Each rule group is one file in `../rules/`, depends only on its tool, and is tested in
github.com/barbulescu/architectural-tests against passing and failing fixtures.

**ArchUnit** reads compiled bytecode: its rules cover Java **and** Kotlin.
**Konsist** reads Kotlin source: use it for Kotlin-only concepts (`val`/`var`, imports, file layout).
**detekt** is static analysis on Kotlin source: its fragments give feedback in the IDE and before the tests run.

## Overview

| Id | Tool | File | Catches | Recommend when |
|---|---|---|---|---|
| `layers` | ArchUnit | `java/LayeredArchitectureRules.java` | web, application, domain and infrastructure packages depending in the wrong direction | packages named like web/application/domain/infrastructure exist |
| `hexagonal` | ArchUnit | `java/HexagonalArchitectureRules.java` | domain depending outwards, adapters depending on each other | `adapter(s)`/`port(s)` packages, or `domain.model` + `domain.service` |
| `cycles` | ArchUnit | `java/CycleRules.java` | dependency cycles between the top-level packages | always (two or more top-level packages) |
| `coding` | ArchUnit | `java/CodingRules.java` | `System.out`, java.util.logging, generic exceptions, Joda-Time, field injection | always; pick the individual methods that fit (see below) |
| `naming` | ArchUnit | `java/NamingRules.java` | a class whose suffix promises a role living in the wrong package | a suffix convention (`Controller`, `Repository`, …) is used consistently |
| `test-presence` | ArchUnit | `java/TestPresenceRules.java` | a class of a kind that must be tested having no `<Name>Test` | a class kind that should always have its own test (`UseCase`, `Service`) |
| `spring` | ArchUnit | `java/SpringRules.java` | controllers using repositories, `@Transactional` outside services | Spring is a dependency |
| `kotlin-immutability` | Konsist | `kotlin/ImmutabilityRules.kt.txt` | `var` and mutable collection properties in e.g. the domain | Kotlin domain/model package with data classes |
| `kotlin-framework-free` | Konsist | `kotlin/FrameworkFreeRules.kt.txt` | framework imports in e.g. the domain | Kotlin domain package in a project that uses a framework |
| `kotlin-file-layout` | Konsist | `kotlin/FileLayoutRules.kt.txt` | a file with one class not named after it | Kotlin project (optional, a convention rule) |
| `detekt-domain-imports` | detekt | `detekt/domain-imports.yml` | domain files importing frameworks or outer layers | Kotlin + Gradle, and detekt used or welcome |

## Details

### `layers`

```java
LayeredArchitectureRules.webApplicationDomainInfrastructure("..web..", "..application..", "..domain..", "..infrastructure..")
```

Web may use application and domain; application may use domain; infrastructure implements domain ports; nothing
uses web or infrastructure. A class outside every layer (the main class, a Spring `@Configuration` in the base
package) is the composition root and may use everything. For a different layering, start from
`LayeredArchitectureRules.layers()` and add `.layer(...)`/`.whereLayer(...)` calls in the test.
Parameters: the four package patterns. The rule text (with `because`) is stable: frozen baselines depend on it.

### `hexagonal`

```java
HexagonalArchitectureRules.hexagonal("..domain.model..", "..domain.service..", "..application..",
        Map.of("rest", "..adapter.rest..", "persistence", "..adapter.persistence.."))
```

Parameters: domain model, domain service and application packages; one entry per adapter. Empty layers are allowed.
Do not select together with `layers` for the same packages.

### `cycles`

```java
CycleRules.noCyclesBetweenTopLevelPackages("com.acme.shop")
```

Parameter: the base package. Each direct sub-package (with everything below it) is a slice.

### `coding`

| Method | Recommend |
|---|---|
| `noStandardStreams()` | always, except CLI tools that print on purpose |
| `noJavaUtilLogging()` | when the project uses SLF4J/Log4j/Logback |
| `noGenericExceptions()` | optional: noisy in older code bases, a good candidate for a baseline |
| `noJodaTime()` | only if Joda-Time is (or was) a dependency |
| `noFieldInjection()` | when a DI framework (Spring, Jakarta, Micronaut) is used |

One test method per selected method.

### `naming`

```java
NamingRules.classesWithSuffixResideIn("Controller", "..web..")
```

Parameters: suffix and package. One test method per convention. Fails when no class has the suffix (catches typos).

### `test-presence`

```java
TestPresenceRules.classesHaveATestClass("..application..", "UseCase", PRODUCTION_AND_TESTS)
```

Checks that `FooUseCase` has a class named `FooUseCaseTest` (any package), not what that test does.
Must be checked against classes imported **including** tests (`PRODUCTION_AND_TESTS` in the template).
Fails when nothing matches (catches a wrong package or suffix).

### `spring`

```java
SpringRules.controllersDoNotAccessRepositories()
SpringRules.transactionalOnlyIn("..service..")
```

Annotations are matched by name; no Spring dependency is needed to compile. Passes when nothing matches.
For field injection use `coding`'s `noFieldInjection()`.

### `kotlin-immutability`

```kotlin
ImmutabilityRules.assertImmutable(production, "com.acme.shop.domain..")
```

No `var`, no `Mutable*`/`ArrayList`/`HashMap`-style types, no `mutableListOf()`-style initialisers, in top-level,
constructor, class, object and interface properties of the package. Local variables are fine.
Existing offenders: `@Suppress("konsist.<test method name>")`.

### `kotlin-framework-free`

```kotlin
FrameworkFreeRules.assertNoImports(production, "com.acme.shop.domain..")                   // common frameworks
FrameworkFreeRules.assertNoImports(production, "com.acme.shop.domain..", listOf("org.springframework."))
```

Reported per import with file and line.

### `kotlin-file-layout`

```kotlin
FileLayoutRules.assertSingleDeclarationFilesAreNamedAfterIt(production)
```

Kotlin coding convention. Files with several top-level classes are ignored.

### `detekt-domain-imports`

A `ForbiddenImport` configuration limited to `**/domain/**`. Merge into `detekt.yml` (see `build-setup.md`) and adapt
the path and patterns. detekt has one `ForbiddenImport` configuration per project, so merge with an existing one.
Gradle only: detekt has no official Maven plugin. Overlaps with `layers` and `kotlin-framework-free` on purpose:
detekt reports in the IDE and pre-commit, the tests are the safety net in CI.

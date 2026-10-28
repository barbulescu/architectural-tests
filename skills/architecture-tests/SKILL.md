---
name: architecture-tests
description: Select and add architecture tests to a Java or Kotlin project (Maven or Gradle). Inspects the code base, recommends rules from a tested catalog (ArchUnit for layers, hexagonal architecture, package cycles, coding rules, naming, test presence and Spring conventions; Konsist and detekt for Kotlin immutability, framework-free domain and file layout), copies the selected rule sources into the project and generates a short test class that applies them. Use when asked to add, choose or update architecture tests or ArchUnit/Konsist/detekt rules, to enforce layers or package dependencies, or to stop a code base from eroding.
---

# Architecture tests

You add architecture tests to the user's project by **copying rule source files** from this skill and generating a
**short test class** that calls them with the project's packages. Nothing is published or downloaded except the
ArchUnit/Konsist test libraries. The user owns the copied code and may edit it.

Files in this skill (paths relative to this file):

| Path | What |
|---|---|
| `references/catalog.md` | Every rule group: what it catches, when to recommend it, parameters, call snippet. **Read it before step 2.** |
| `references/build-setup.md` | Dependency and build snippets for Maven and Gradle. |
| `rules/java/*.java`, `rules/kotlin/*.kt.txt`, `rules/detekt/*.yml` | The rule sources to copy. One file per rule group. |
| `templates/` | `ArchitectureTest.java`, `KotlinArchitectureTest.kt.txt`, `archunit.properties`. |

Work through the steps in order. Ask the user where a step says so; otherwise proceed.

## 1. Inspect the project

Find out, by reading files (do not guess):

- **Build tool**: `pom.xml` (Maven) or `build.gradle(.kts)` (Gradle). Multi-module: ask which module(s).
- **Languages**: `src/main/java`, `src/main/kotlin`. Kotlin rules only make sense where Kotlin production code exists.
- **Test framework**: the rules need JUnit 5 (Jupiter). With only JUnit 4, tell the user and offer to add
  `junit-jupiter` plus `junit-vintage-engine` so existing tests keep running. Stop if they decline.
- **Java version**: ArchUnit 1.5 needs Java 8+, Konsist needs Kotlin/JVM 11+. Note the version for the snippets.
- **Root package**: the longest package prefix shared by all production classes, e.g. `com.acme.shop`. Every rule
  is applied to this package and its sub-packages only. If the user wants the rules for part of the code base only
  (one bounded context, one module's packages), use that package as the root.
- **Top-level sub-packages** of the root package and what they suggest:
  - `web`/`api`/`rest`/`controller`, `application`/`usecase`/`service`, `domain`/`model`, `infrastructure`/`persistence` → **layered**
  - `adapter(s)`, `port(s)`, `domain` (often `domain.model`, `domain.service`) → **hexagonal**
  - feature packages (`order`, `customer`, …) → neither; recommend `cycles` and `coding`, and ask about layering inside features.
- **Frameworks**: Spring (`spring-boot`, `spring-context` in the build file), Jakarta EE, Micronaut, Quarkus.
- **Naming conventions** actually used: class name suffixes like `Controller`, `UseCase`, `Service`, `Repository`.
- **Existing setup**: ArchUnit/Konsist/detekt dependencies, `archunit_store/`, `detekt.yml`, earlier copies of these
  rules (files starting with `// From github.com/barbulescu/architectural-tests`). If copies exist, go to step 7 first.

Summarise the findings to the user in a few lines.

## 2. Recommend rules

Match the findings against the "Recommend when" column of `references/catalog.md`. Present two short lists:
**recommended** (signals clearly match) and **optional** (might fit), one line each with the reason taken from this
project ("`web` and `infrastructure` packages exist, so…"). Do not offer rules whose signals are absent (no Spring
rules without Spring, no Kotlin rules without Kotlin, no detekt without Gradle).

Ask the user to confirm or change the selection.

## 3. Parameters

Propose the root package and, for each selected rule, concrete values inferred in step 1 (layer packages, the suffix
and package for `test-presence`, forbidden import prefixes), and ask the user to confirm them in one round.
Every rule takes the root package first; all other packages are **relative to it** and include their sub-packages:
`"web"` for `com.acme.shop.web..`, `"domain.model"` for `com.acme.shop.domain.model..`, `""` for the root itself.
Do not pass ArchUnit (`..web..`) or Konsist (`com.acme.shop.web..`) patterns.

## 4. Copy the rule files

For each selected rule group, copy its file from `rules/`:

- Java: `rules/java/<File>.java` → `src/test/java/<root package path>/architecture/rules/<File>.java`
- Kotlin: `rules/kotlin/<File>.kt.txt` → `src/test/kotlin/<root package path>/architecture/rules/<File>.kt` (drop `.txt`)
- detekt: merge `rules/detekt/<file>.yml` into the project's `detekt.yml` (create `config/detekt/detekt.yml` if none)
  and replace the example root package `com.acme.shop` in its path and patterns.

In every copied source file change only the `package` line to `<root package>.architecture.rules`. Keep the first
comment line: it identifies the source and lets step 7 find the copies later. Copy rule files byte for byte
otherwise: rule texts are part of frozen baselines.

## 5. Wire the build and generate the tests

1. Add the missing test dependencies and build configuration from `references/build-setup.md`. Use the versions
   given there unless the project already pins newer ones. Do not touch unrelated parts of the build.
2. Generate `src/test/java/<root package path>/architecture/ArchitectureTest.java` from
   `templates/ArchitectureTest.java` and/or `src/test/kotlin/<root package path>/architecture/KotlinArchitectureTest.kt`
   from `templates/KotlinArchitectureTest.kt.txt`. Replace the `{{…}}` placeholders, keep only the test methods whose
   `// [rule-id]` marker is selected, and remove imports and fields that are no longer used.
   Keep the test method names from the template unless the user asks otherwise: Konsist suppressions
   (`@Suppress("konsist.<test method name>")`) refer to them.
3. Copy `templates/archunit.properties` to `src/test/resources/archunit.properties` if the project has none.
   Add `archunit_store/` to version control instructions in your summary (it must be committed, not ignored).

## 6. Run the tests and deal with violations

Run only the new tests first (`./mvnw test -Dtest='ArchitectureTest,KotlinArchitectureTest'` or
`./gradlew test --tests '*ArchitectureTest'`; use `mvn`/`gradle` if there is no wrapper). Then:

- **Compile errors**: fix them (usually a missing dependency or a wrong import); do not change rule logic.
- **Rule failed because nothing matched** (`failed to check any classes`): a parameter is wrong (root package,
  sub-package or suffix). Re-check it with the user.
- **Real violations**: show them grouped by rule, with counts. For each rule, ask the user to choose:
  1. **fix the code** now (only if there are a few and the fix is obvious);
  2. **adjust the rule** if it does not match the intended architecture (e.g. a layer is named differently);
  3. **record a baseline**. ArchUnit: wrap the rule in `FreezingArchRule.freeze(...)` in `ArchitectureTest`, run once
     with `-Darchunit.freeze.refreeze=true` to record the existing violations in `archunit_store/`, then run normally.
     New violations fail; fixed ones leave the store automatically. Konsist: add
     `@Suppress("konsist.<test method name>")` on each existing offender.

  Never delete or weaken a rule, or widen a package pattern, just to make the build green without the user's say-so.

Finish by running the whole test suite once, so you know nothing else broke.

## 7. Updating earlier copies

If the project already contains copied rule files: compare each with the current file in `rules/` (ignoring the
`package` line) and show the user what changed. Changes the project made itself are not upstream changes: point
them out and ask before overwriting. Never change a rule's description text silently: for frozen rules that resets
the baseline. If a text changes, tell the user that the next run needs `-Darchunit.freeze.refreeze=true` and why.

## Summary for the user

End with: the rules added (one line each), the files created or changed, how to run them, and, if a baseline
was recorded, that `archunit_store/` must be committed and that re-baselining is a deliberate
`-Darchunit.freeze.refreeze=true`.

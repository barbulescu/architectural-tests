# Examples

The same small code base, mostly Java with a Kotlin domain model, guarded by the same architecture tests,
in the two build tools people use:

| Example | Run | Notes |
|---|---|---|
| [`shop-maven`](shop-maven) | `./mvnw test` | Maven wrapper downloads Maven itself |
| [`shop-gradle`](shop-gradle) | `./gradlew test` | Gradle wrapper downloads Gradle itself |

Everything is a plain JUnit test in a standard build: the test command is the only one anyone needs.
Both are standalone projects and require JDK 21+.

```
com.example.shop
├── domain          Kotlin   Order, Money, OrderRepository (port)
├── application     Java     PlaceOrderUseCase
├── infrastructure  Java     InMemoryOrderRepository (adapter)
├── web             Java     OrderController
└── ShopApplication Java     composition root, the only class that knows every layer
```

## Which tool checks what

| Rule | Tool | Why that tool |
|---|---|---|
| Layer dependencies (all code) | ArchUnit `ArchitectureTest.layers_are_respected` | Reads bytecode, so it covers Java **and** Kotlin |
| No package cycles, no `System.out`, no JUL, controllers in `web` | ArchUnit `ArchitectureTest` | Ready-made rules in `GeneralCodingRules` |
| Every use case has a test | ArchUnit `ArchitectureTest.every_use_case_has_a_test` | A custom `ArchCondition`: a few lines of plain Java |
| Kotlin domain properties are `val` and not mutable collections, no framework imports | Konsist `KotlinArchitectureTest` | `val`/`var` and imports are source concepts |

ArchUnit is the main tool: it covers all of the code. Konsist is the add-on for the Kotlin part.

The rules themselves are in `src/test/{java,kotlin}/com/example/shop/architecture/rules/`, copied from this repo's
rule catalog by the [`architecture-tests` skill](../skills/architecture-tests/SKILL.md). The two test classes only
apply them to this project's packages.

## Adopting on an existing code base

* **ArchUnit**: wrap the rule in `FreezingArchRule.freeze(...)`. The first run records existing violations in
  `archunit_store/` (commit it). New violations fail; fixed ones are removed from the store automatically, so
  the baseline can only shrink. `freeze.refreeze=false` in `archunit.properties` stops anyone from quietly
  growing it; re-baselining is a deliberate `-Darchunit.freeze.refreeze=true` (Maven and Gradle alike) and shows up in the diff.
* **Konsist**: no store. Mark existing offenders with `@Suppress("konsist.<test name>")`. Every exception
  is visible where it lives and countable with `git grep konsist.`.

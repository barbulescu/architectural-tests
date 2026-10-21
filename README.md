# Architecture tests with ArchUnit and Konsist

A small code base, mostly Java with a Kotlin domain model (roughly the 80/20 split of a typical
JVM shop), used to demo architecture tests to a skeptical audience.
Everything is a plain JUnit test in a standard Maven build: `./mvnw test` is the only command anyone needs.
Requires JDK 21+; the Maven wrapper downloads Maven itself.

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
| Layer dependencies (all code) | ArchUnit `LayerRulesTest` | Reads bytecode, so it covers Java **and** Kotlin |
| No package cycles, no `System.out`, no JUL, controllers in `web` | ArchUnit `CodingRulesTest` | Ready-made rules in `GeneralCodingRules` |
| Every use case has a test | ArchUnit `TestCoverageRulesTest` | A custom `ArchCondition`: a few lines of plain Java |
| Kotlin domain properties are `val`, no framework imports | Konsist `DomainModelKonsistTest` | `val`/`var` and imports are source concepts |

ArchUnit is the main tool: it covers all of the code. Konsist is the add-on for the Kotlin part.

## Live demo runbook

```bash
./mvnw test                                   # 1. green: rules pass on the clean code base

./demo.sh apply 01                            # 2. web -> infrastructure shortcut (Java)
./mvnw test                                   #    ArchUnit fails, names the constructor and field
./demo.sh reset

./demo.sh apply 02                            # 3. `var` in a domain data class (Kotlin)
./mvnw test                                   #    Konsist fails with file:line
./demo.sh reset

./demo.sh apply 03                            # 4. use case without a test (Java)
./mvnw test                                   #    ArchUnit custom rule fails: "every use case has a test"
./demo.sh reset

./demo.sh apply 04                            # 5. "our legacy code would fail everything"
./mvnw test                                   #    fails: legacy violations
./mvnw test -Darchunit.freeze.refreeze=true   #    record them as the baseline in archunit_store/
./mvnw test                                   #    green: the past is tolerated...
./demo.sh apply 01
./mvnw test                                   #    ...but the new shortcut still fails
./demo.sh reset
```

Each scenario has a `README.md` in `demo/scenarios/` with the talking point.
Kotlin scenario files are stored as `*.kt.scenario` because Konsist scans every `.kt` file in the project.

## Adopting on an existing code base

* **ArchUnit**: wrap the rule in `FreezingArchRule.freeze(...)`. The first run records existing violations in
  `archunit_store/` (commit it). New violations fail; fixed ones are removed from the store automatically, so
  the baseline can only shrink. `freeze.refreeze=false` in `archunit.properties` stops anyone from quietly
  growing it; re-baselining is a deliberate `-Darchunit.freeze.refreeze=true` and shows up in the diff.
* **Konsist**: no store. Mark existing offenders with `@Suppress("konsist.<test name>")`. Every exception
  is visible where it lives and countable with `git grep konsist.`.

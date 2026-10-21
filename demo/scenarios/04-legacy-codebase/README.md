# 04 - "Our legacy code would fail everything"

Simulates an existing code base that already breaks the rules in several places.
The point: you do not have to fix the past to stop making it worse.

* ArchUnit: `./mvnw test -Darchunit.freeze.refreeze=true` records today's violations in `archunit_store/` (commit it).
  From then on only *new* violations fail, and fixed ones drop out of the store automatically.
* Konsist: no store; existing offenders are marked explicitly with `@Suppress("konsist.<test name>")`,
  so every exception is visible right where it lives and shows up in `git grep`.

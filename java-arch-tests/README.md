# java-arch-tests

Reusable ArchUnit rules, one class per rule group in `src/main/java/com/example/archtests/`. ArchUnit analyses
compiled bytecode, so the rules apply to Java and Kotlin code alike.

Projects do not depend on this module: the [`architecture-tests` skill](../skills/architecture-tests/SKILL.md)
copies the rule files they select. The rules are tested here against passing and failing fixtures in
`src/test/java/com/example/archtests/fixtures/<group>/{valid,invalid}`. See the
[catalog](../skills/architecture-tests/references/catalog.md) for what each group checks.

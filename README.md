# Architecture tests with ArchUnit, Konsist and detekt

A tested catalog of architecture rules, and an agent skill that picks the ones that fit a project and adds them.

```
.
├── java-arch-tests        ArchUnit rule groups (bytecode, so Java and Kotlin), with fixture tests
├── kotlin-arch-tests      Konsist rule groups and detekt config fragments (Kotlin source), with fixture tests
├── skills
│   └── architecture-tests agent skill: inspects a project, recommends rules, copies them in, generates the tests
├── examples
│   ├── shop-maven         example project (Java + Kotlin domain) set up by the skill, Maven build
│   └── shop-gradle        the same example, Gradle build
└── presentation           live-demo runbook, scenarios and demo.sh
```

## Using the skill

The skill works in GitHub Copilot (VS Code agent mode, Copilot CLI, cloud agent) and in Claude Code. Copy
`skills/architecture-tests/` into one of:

| Location | Picked up by |
|---|---|
| `<your project>/.claude/skills/architecture-tests/` | Copilot and Claude Code, for that project |
| `<your project>/.github/skills/architecture-tests/` | Copilot, for that project |
| `~/.claude/skills/architecture-tests/` | Copilot and Claude Code, for all your projects |

Then ask the agent to "add architecture tests". It copies the selected rule sources into your project's test
sources: there is no library to depend on, and the copied code is yours to edit. Re-running it later compares your
copies with the current catalog.

## Working on the catalog

Every rule takes a root package and checks only that package and its sub-packages; other package parameters are
relative to it (`"web"` is `<root>.web..`). Each rule's tests include fixtures outside the root that must be ignored.

`./gradlew build` runs every rule against its fixtures (JDK 21+). After changing a rule, run
`./gradlew bundleSkill` to refresh the skill's copy in `skills/architecture-tests/rules/`; CI fails if it is stale.
The examples are standalone projects: `cd examples/shop-maven && ./mvnw test`, or `cd examples/shop-gradle && ./gradlew test`.

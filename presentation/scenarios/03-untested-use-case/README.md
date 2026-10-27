# 03 - The use case nobody tested

A new use case lands without a test. Reviewers see the code that is there; they rarely notice the file that is not.

Caught by: **ArchUnit** (`TestCoverageRulesTest.every_use_case_has_a_test`), a custom rule: when the library has
no ready-made rule, the condition is a few lines of plain Java.

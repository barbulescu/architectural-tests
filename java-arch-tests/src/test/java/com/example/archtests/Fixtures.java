package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Each rule group has fixtures in {@code fixtures.<group>.valid} and {@code fixtures.<group>.invalid}. */
final class Fixtures {

    static final String ROOT = "com.example.archtests.fixtures";

    private Fixtures() {
    }

    static String pkg(String group, String variant) {
        return ROOT + "." + group + "." + variant;
    }

    /** Both variants: for checking that a rule rooted at one variant ignores the other. */
    static JavaClasses classes(String group) {
        return new ClassFileImporter().importPackages(ROOT + "." + group);
    }

    static JavaClasses classes(String group, String variant) {
        return new ClassFileImporter().importPackages(pkg(group, variant));
    }

    static void assertPasses(ArchRule rule, JavaClasses classes) {
        EvaluationResult result = rule.evaluate(classes);
        assertFalse(result.hasViolation(), () -> result.getFailureReport().toString());
    }

    /** Fails unless the rule is violated and every expected fragment appears in the failure report. */
    static void assertViolates(ArchRule rule, JavaClasses classes, String... expectedFragments) {
        EvaluationResult result = rule.evaluate(classes);
        assertTrue(result.hasViolation(), "expected a violation of: " + rule.getDescription());
        String report = result.getFailureReport().toString();
        for (String fragment : expectedFragments) {
            assertTrue(report.contains(fragment), () -> "expected '" + fragment + "' in:\n" + report);
        }
    }
}

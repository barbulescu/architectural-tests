package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

class CodingRulesTest {

    private static final JavaClasses VALID = classes("coding", "valid");
    private static final JavaClasses INVALID = classes("coding", "invalid");
    private static final String VALID_ROOT = pkg("coding", "valid");
    private static final String INVALID_ROOT = pkg("coding", "invalid");

    @Test
    void clean_code_passes_every_rule() {
        assertPasses(CodingRules.noStandardStreams(VALID_ROOT), VALID);
        assertPasses(CodingRules.noJavaUtilLogging(VALID_ROOT), VALID);
        assertPasses(CodingRules.noGenericExceptions(VALID_ROOT), VALID);
        assertPasses(CodingRules.noJodaTime(VALID_ROOT), VALID);
        assertPasses(CodingRules.noFieldInjection(VALID_ROOT), VALID);
    }

    @Test
    void violations_outside_the_root_package_are_ignored() {
        JavaClasses both = classes("coding");
        assertPasses(CodingRules.noStandardStreams(VALID_ROOT), both);
        assertPasses(CodingRules.noJavaUtilLogging(VALID_ROOT), both);
        assertPasses(CodingRules.noGenericExceptions(VALID_ROOT), both);
        assertPasses(CodingRules.noFieldInjection(VALID_ROOT), both);
    }

    @Test
    void printing_to_the_console_fails() {
        assertViolates(CodingRules.noStandardStreams(INVALID_ROOT), INVALID, "Printing.run()", "System.out");
    }

    @Test
    void java_util_logging_fails() {
        assertViolates(CodingRules.noJavaUtilLogging(INVALID_ROOT), INVALID, "JulLogging.LOG");
    }

    @Test
    void throwing_a_generic_exception_fails() {
        assertViolates(CodingRules.noGenericExceptions(INVALID_ROOT), INVALID, "GenericException.fail()", "RuntimeException");
    }

    @Test
    void field_injection_fails() {
        assertViolates(CodingRules.noFieldInjection(INVALID_ROOT), INVALID, "FieldInjected.dependency");
    }
}

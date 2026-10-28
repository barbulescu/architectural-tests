package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;

class CodingRulesTest {

    private static final JavaClasses VALID = classes("coding", "valid");
    private static final JavaClasses INVALID = classes("coding", "invalid");

    @Test
    void clean_code_passes_every_rule() {
        assertPasses(CodingRules.noStandardStreams(), VALID);
        assertPasses(CodingRules.noJavaUtilLogging(), VALID);
        assertPasses(CodingRules.noGenericExceptions(), VALID);
        assertPasses(CodingRules.noJodaTime(), VALID);
        assertPasses(CodingRules.noFieldInjection(), VALID);
    }

    @Test
    void printing_to_the_console_fails() {
        assertViolates(CodingRules.noStandardStreams(), INVALID, "Printing.run()", "System.out");
    }

    @Test
    void java_util_logging_fails() {
        assertViolates(CodingRules.noJavaUtilLogging(), INVALID, "JulLogging.LOG");
    }

    @Test
    void throwing_a_generic_exception_fails() {
        assertViolates(CodingRules.noGenericExceptions(), INVALID, "GenericException.fail()", "RuntimeException");
    }

    @Test
    void field_injection_fails() {
        assertViolates(CodingRules.noFieldInjection(), INVALID, "FieldInjected.dependency");
    }
}

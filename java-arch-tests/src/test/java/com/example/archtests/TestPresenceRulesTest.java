package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

class TestPresenceRulesTest {

    @Test
    void use_case_with_a_test_passes() {
        JavaClasses classes = classes("testpresence", "valid");
        assertPasses(TestPresenceRules.classesHaveATestClass(pkg("testpresence", "valid"), "", "UseCase", classes), classes);
    }

    @Test
    void use_case_without_a_test_fails() {
        JavaClasses classes = classes("testpresence", "invalid");
        assertViolates(TestPresenceRules.classesHaveATestClass(pkg("testpresence", "invalid"), "", "UseCase", classes), classes,
                "CancelOrderUseCase has no CancelOrderUseCaseTest");
    }

    @Test
    void classes_outside_the_root_package_are_ignored() {
        JavaClasses classes = classes("testpresence");
        assertPasses(TestPresenceRules.classesHaveATestClass(pkg("testpresence", "valid"), "", "UseCase", classes), classes);
    }
}

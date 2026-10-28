package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;

class TestPresenceRulesTest {

    @Test
    void use_case_with_a_test_passes() {
        JavaClasses classes = classes("testpresence", "valid");
        assertPasses(TestPresenceRules.classesHaveATestClass("..testpresence..", "UseCase", classes), classes);
    }

    @Test
    void use_case_without_a_test_fails() {
        JavaClasses classes = classes("testpresence", "invalid");
        assertViolates(TestPresenceRules.classesHaveATestClass("..testpresence..", "UseCase", classes), classes,
                "CancelOrderUseCase has no CancelOrderUseCaseTest");
    }
}

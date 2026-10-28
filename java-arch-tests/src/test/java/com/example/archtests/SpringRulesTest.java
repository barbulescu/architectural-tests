package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

/** The Spring annotations used here are stand-ins in {@code org.springframework.*} test sources: rules match by name. */
class SpringRulesTest {

    private static final JavaClasses VALID = classes("spring", "valid");
    private static final JavaClasses INVALID = classes("spring", "invalid");
    private static final String VALID_ROOT = pkg("spring", "valid");
    private static final String INVALID_ROOT = pkg("spring", "invalid");

    @Test
    void controllers_going_through_services_pass() {
        assertPasses(SpringRules.controllersDoNotAccessRepositories(VALID_ROOT), VALID);
        assertPasses(SpringRules.transactionalOnlyIn(VALID_ROOT, "service"), VALID);
    }

    @Test
    void violations_outside_the_root_package_are_ignored() {
        JavaClasses both = classes("spring");
        assertPasses(SpringRules.controllersDoNotAccessRepositories(VALID_ROOT), both);
        assertPasses(SpringRules.transactionalOnlyIn(VALID_ROOT, "service"), both);
    }

    @Test
    void controller_using_a_repository_fails() {
        assertViolates(SpringRules.controllersDoNotAccessRepositories(INVALID_ROOT), INVALID,
                "OrderController.orders", "CustomerController.customers");
    }

    @Test
    void transactional_outside_the_service_layer_fails() {
        assertViolates(SpringRules.transactionalOnlyIn(INVALID_ROOT, "service"), INVALID, "CustomerController.update()");
    }
}

package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;

/** The Spring annotations used here are stand-ins in {@code org.springframework.*} test sources: rules match by name. */
class SpringRulesTest {

    private static final JavaClasses VALID = classes("spring", "valid");
    private static final JavaClasses INVALID = classes("spring", "invalid");

    @Test
    void controllers_going_through_services_pass() {
        assertPasses(SpringRules.controllersDoNotAccessRepositories(), VALID);
        assertPasses(SpringRules.transactionalOnlyIn("..service.."), VALID);
    }

    @Test
    void controller_using_a_repository_fails() {
        assertViolates(SpringRules.controllersDoNotAccessRepositories(), INVALID,
                "OrderController.orders", "CustomerController.customers");
    }

    @Test
    void transactional_outside_the_service_layer_fails() {
        assertViolates(SpringRules.transactionalOnlyIn("..service.."), INVALID, "CustomerController.update()");
    }
}

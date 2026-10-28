package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LayeredArchitectureRulesTest {

    private static ArchRule rule(String rootPackage) {
        return LayeredArchitectureRules.webApplicationDomainInfrastructure(
                rootPackage, "web", "application", "domain", "infrastructure");
    }

    @Test
    void web_through_use_cases_and_infrastructure_wired_by_the_composition_root_passes() {
        assertPasses(rule(pkg("layers", "valid")), classes("layers", "valid"));
    }

    @Test
    void web_reaching_into_infrastructure_fails() {
        assertViolates(rule(pkg("layers", "invalid")), classes("layers", "invalid"),
                "Field <com.example.archtests.fixtures.layers.invalid.web.OrderController.orders>",
                "InMemoryOrders");
    }

    @Test
    void violations_outside_the_root_package_are_ignored() {
        assertPasses(rule(pkg("layers", "valid")), classes("layers"));
    }

    /** Projects freeze this rule, and the freeze store is keyed by the rule text: changing it resets every baseline. */
    @Test
    void rule_text_is_stable() {
        assertEquals("""
                Layered architecture considering only dependencies in layers, consisting of
                layer 'Web' ('com.acme.shop.web..')
                layer 'Application' ('com.acme.shop.application..')
                layer 'Domain' ('com.acme.shop.domain..')
                layer 'Infrastructure' ('com.acme.shop.infrastructure..')
                where layer 'Web' may not be accessed by any layer
                where layer 'Application' may only be accessed by layers ['Web']
                where layer 'Domain' may only be accessed by layers ['Application', 'Infrastructure', 'Web']
                where layer 'Infrastructure' may not be accessed by any layer, \
                because the web layer talks to use cases, and only the composition root wires in infrastructure""",
                rule("com.acme.shop").getDescription());
    }
}

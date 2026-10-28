package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LayeredArchitectureRulesTest {

    private static final ArchRule RULE =
            LayeredArchitectureRules.webApplicationDomainInfrastructure("..web..", "..application..", "..domain..", "..infrastructure..");

    @Test
    void web_through_use_cases_and_infrastructure_wired_by_the_composition_root_passes() {
        assertPasses(RULE, classes("layers", "valid"));
    }

    @Test
    void web_reaching_into_infrastructure_fails() {
        assertViolates(RULE, classes("layers", "invalid"),
                "Field <com.example.archtests.fixtures.layers.invalid.web.OrderController.orders>",
                "InMemoryOrders");
    }

    /** Projects freeze this rule, and the freeze store is keyed by the rule text: changing it resets every baseline. */
    @Test
    void rule_text_is_stable() {
        assertEquals("""
                Layered architecture considering only dependencies in layers, consisting of
                layer 'Web' ('..web..')
                layer 'Application' ('..application..')
                layer 'Domain' ('..domain..')
                layer 'Infrastructure' ('..infrastructure..')
                where layer 'Web' may not be accessed by any layer
                where layer 'Application' may only be accessed by layers ['Web']
                where layer 'Domain' may only be accessed by layers ['Application', 'Infrastructure', 'Web']
                where layer 'Infrastructure' may not be accessed by any layer, \
                because the web layer talks to use cases, and only the composition root wires in infrastructure""",
                RULE.getDescription());
    }
}

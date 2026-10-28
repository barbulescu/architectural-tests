package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

class HexagonalArchitectureRulesTest {

    private static ArchRule rule(String rootPackage) {
        return HexagonalArchitectureRules.hexagonal(rootPackage, "domain.model", "domain.service", "application",
                Map.of("rest", "adapter.rest", "persistence", "adapter.persistence"));
    }

    @Test
    void adapters_depending_inwards_pass() {
        assertPasses(rule(pkg("hexagonal", "valid")), classes("hexagonal", "valid"));
    }

    @Test
    void adapter_depending_on_another_adapter_fails() {
        assertViolates(rule(pkg("hexagonal", "invalid")), classes("hexagonal", "invalid"), "OrderTable.resource", "OrderResource");
    }

    @Test
    void violations_outside_the_root_package_are_ignored() {
        assertPasses(rule(pkg("hexagonal", "valid")), classes("hexagonal"));
    }
}

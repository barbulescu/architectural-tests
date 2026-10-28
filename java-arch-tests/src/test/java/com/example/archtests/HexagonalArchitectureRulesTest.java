package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;

class HexagonalArchitectureRulesTest {

    private static final ArchRule RULE = HexagonalArchitectureRules.hexagonal(
            "..domain.model..", "..domain.service..", "..application..",
            Map.of("rest", "..adapter.rest..", "persistence", "..adapter.persistence.."));

    @Test
    void adapters_depending_inwards_pass() {
        assertPasses(RULE, classes("hexagonal", "valid"));
    }

    @Test
    void adapter_depending_on_another_adapter_fails() {
        assertViolates(RULE, classes("hexagonal", "invalid"), "OrderTable.resource", "OrderResource");
    }
}

package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;

class NamingRulesTest {

    private static final ArchRule RULE = NamingRules.classesWithSuffixResideIn("Controller", "..web..");

    @Test
    void controller_in_web_passes() {
        assertPasses(RULE, classes("naming", "valid"));
    }

    @Test
    void controller_elsewhere_fails() {
        assertViolates(RULE, classes("naming", "invalid"), "naming.invalid.service.OrderController");
    }
}

package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

class NamingRulesTest {

    private static ArchRule rule(String rootPackage) {
        return NamingRules.classesWithSuffixResideIn(rootPackage, "Controller", "web");
    }

    @Test
    void controller_in_web_passes() {
        assertPasses(rule(pkg("naming", "valid")), classes("naming", "valid"));
    }

    @Test
    void controller_elsewhere_fails() {
        assertViolates(rule(pkg("naming", "invalid")), classes("naming", "invalid"), "naming.invalid.service.OrderController");
    }

    @Test
    void classes_outside_the_root_package_are_ignored() {
        assertPasses(rule(pkg("naming", "valid")), classes("naming"));
    }
}

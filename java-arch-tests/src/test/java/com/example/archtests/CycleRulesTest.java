package com.example.archtests;

import org.junit.jupiter.api.Test;

import static com.example.archtests.Fixtures.assertPasses;
import static com.example.archtests.Fixtures.assertViolates;
import static com.example.archtests.Fixtures.classes;
import static com.example.archtests.Fixtures.pkg;

class CycleRulesTest {

    @Test
    void one_way_dependency_passes() {
        assertPasses(CycleRules.noCyclesBetweenTopLevelPackages(pkg("cycles", "valid")), classes("cycles", "valid"));
    }

    @Test
    void two_packages_depending_on_each_other_fail() {
        assertViolates(CycleRules.noCyclesBetweenTopLevelPackages(pkg("cycles", "invalid")), classes("cycles", "invalid"),
                "Cycle detected: Slice a -> \n                Slice b -> \n                Slice a");
    }
}

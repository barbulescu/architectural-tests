// From github.com/barbulescu/architectural-tests, rule group "cycles". Copied into your project: edit freely.
package com.example.shop.architecture.rules;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/** Cycles between packages make them impossible to understand, test or extract on their own. */
public final class CycleRules {

    private CycleRules() {
    }

    /** Each direct sub-package of {@code basePackage} (with everything below it) is one slice. */
    public static ArchRule noCyclesBetweenTopLevelPackages(String basePackage) {
        return slices().matching(basePackage + ".(*)..").should().beFreeOfCycles();
    }
}

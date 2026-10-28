// From github.com/barbulescu/architectural-tests, rule group "naming". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/** Names that promise a role should match where the class lives, e.g. controllers in the web layer. */
public final class NamingRules {

    private NamingRules() {
    }

    public static ArchRule classesWithSuffixResideIn(String simpleNameSuffix, String packagePattern) {
        return classes().that().haveSimpleNameEndingWith(simpleNameSuffix)
                .should().resideInAPackage(packagePattern);
    }
}

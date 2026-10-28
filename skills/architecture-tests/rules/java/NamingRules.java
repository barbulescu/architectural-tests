// From github.com/barbulescu/architectural-tests, rule group "naming". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/** Names that promise a role should match where the class lives, e.g. controllers in the web layer. */
public final class NamingRules {

    private NamingRules() {
    }

    /**
     * Classes below {@code rootPackage} whose name ends with {@code simpleNameSuffix} reside in
     * {@code <rootPackage>.<subPackage>} or its sub-packages.
     */
    public static ArchRule classesWithSuffixResideIn(String rootPackage, String simpleNameSuffix, String subPackage) {
        return classes().that().resideInAPackage(rootPackage + "..").and().haveSimpleNameEndingWith(simpleNameSuffix)
                .should().resideInAPackage(inside(rootPackage, subPackage));
    }

    private static String inside(String rootPackage, String subPackage) {
        return subPackage.isEmpty() ? rootPackage + ".." : rootPackage + "." + subPackage + "..";
    }
}

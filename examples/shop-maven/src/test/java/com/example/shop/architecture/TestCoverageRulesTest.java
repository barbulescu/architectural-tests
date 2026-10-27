package com.example.shop.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/** A custom rule: when the library has no ready-made rule, the condition is a few lines of plain Java. */
class TestCoverageRulesTest {

    // Production AND test classes: the rule needs to see both sides.
    private static final JavaClasses ALL_CLASSES = new ClassFileImporter().importPackages("com.example.shop");

    @Test
    void every_use_case_has_a_test() {
        classes().that().resideInAPackage("..application..").and().haveSimpleNameEndingWith("UseCase")
                .should(haveATestClass())
                .check(ALL_CLASSES);
    }

    private static ArchCondition<JavaClass> haveATestClass() {
        return new ArchCondition<>("have a test class named <UseCase>Test") {
            @Override
            public void check(JavaClass useCase, ConditionEvents events) {
                if (!ALL_CLASSES.contain(useCase.getName() + "Test")) {
                    events.add(SimpleConditionEvent.violated(useCase,
                            useCase.getSimpleName() + " has no " + useCase.getSimpleName() + "Test"));
                }
            }
        };
    }
}

// From github.com/barbulescu/architectural-tests, rule group "test-presence". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Reviewers see the code that is there; they rarely notice the test that is not. This checks that a test class
 * exists by naming convention ({@code FooUseCase} needs {@code FooUseCaseTest} in any package), not what it tests.
 */
public final class TestPresenceRules {

    private TestPresenceRules() {
    }

    /**
     * Top-level classes in {@code <rootPackage>.<subPackage>} (or its sub-packages; {@code ""} for the whole root
     * package) whose name ends with {@code simpleNameSuffix} have a test class.
     *
     * @param productionAndTestClasses classes imported WITHOUT {@code DO_NOT_INCLUDE_TESTS}: the rule needs both sides.
     *                                 Check the rule against the same classes.
     */
    public static ArchRule classesHaveATestClass(
            String rootPackage, String subPackage, String simpleNameSuffix, JavaClasses productionAndTestClasses) {
        String packagePattern = subPackage.isEmpty() ? rootPackage + ".." : rootPackage + "." + subPackage + "..";
        return classes().that().resideInAPackage(packagePattern).and().haveSimpleNameEndingWith(simpleNameSuffix)
                .and().areTopLevelClasses()
                .should(haveATestClassIn(productionAndTestClasses));
    }

    private static ArchCondition<JavaClass> haveATestClassIn(JavaClasses allClasses) {
        return new ArchCondition<>("have a test class named <Name>Test") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                String testName = javaClass.getSimpleName() + "Test";
                boolean hasTest = allClasses.stream().anyMatch(c -> c.getSimpleName().equals(testName));
                if (!hasTest) {
                    events.add(SimpleConditionEvent.violated(javaClass,
                            javaClass.getSimpleName() + " has no " + testName));
                }
            }
        };
    }
}

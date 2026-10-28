// From github.com/barbulescu/architectural-tests, rule group "spring". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

/**
 * Spring conventions. Annotations are referenced by name, so this file compiles without Spring on the classpath.
 * For field injection see {@code CodingRules.noFieldInjection()}.
 *
 * <p>The rules pass when nothing matches (no controllers, no {@code @Transactional}): not every Spring application has them.
 */
public final class SpringRules {

    private static final String CONTROLLER = "org.springframework.stereotype.Controller";
    private static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    private static final String REPOSITORY = "org.springframework.stereotype.Repository";
    private static final String DATA_REPOSITORY = "org.springframework.data.repository.Repository";
    private static final String SPRING_TRANSACTIONAL = "org.springframework.transaction.annotation.Transactional";
    private static final String JAKARTA_TRANSACTIONAL = "jakarta.transaction.Transactional";

    private SpringRules() {
    }

    public static ArchRule controllersDoNotAccessRepositories() {
        return noClasses().that().areAnnotatedWith(CONTROLLER).or().areAnnotatedWith(REST_CONTROLLER)
                .should().dependOnClassesThat().areAnnotatedWith(REPOSITORY)
                .orShould().dependOnClassesThat().areAssignableTo(DATA_REPOSITORY)
                .because("controllers go through a service, which owns the transaction and the business rules")
                .allowEmptyShould(true);
    }

    /** {@code @Transactional} (Spring or Jakarta) on classes or methods only inside {@code servicePackage}. */
    public static ArchRule transactionalOnlyIn(String servicePackage) {
        String reason = "transaction boundaries belong to the service layer";
        return CompositeArchRule
                .of(noClasses().that().areAnnotatedWith(SPRING_TRANSACTIONAL).or().areAnnotatedWith(JAKARTA_TRANSACTIONAL)
                        .should().resideOutsideOfPackage(servicePackage).because(reason).allowEmptyShould(true))
                .and(noMethods().that().areAnnotatedWith(SPRING_TRANSACTIONAL).or().areAnnotatedWith(JAKARTA_TRANSACTIONAL)
                        .should().beDeclaredInClassesThat().resideOutsideOfPackage(servicePackage).because(reason)
                        .allowEmptyShould(true));
    }
}

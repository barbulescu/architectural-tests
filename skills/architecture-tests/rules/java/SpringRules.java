// From github.com/barbulescu/architectural-tests, rule group "spring". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

/**
 * Spring conventions. Annotations are referenced by name, so this file compiles without Spring on the classpath.
 * For field injection see {@code CodingRules.noFieldInjection()}.
 *
 * <p>Every rule applies to {@code rootPackage} and its sub-packages only, e.g. {@code "com.acme.shop"}.
 * The rules pass when nothing matches (no controllers, no {@code @Transactional}): not every Spring application has them.
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

    public static ArchRule controllersDoNotAccessRepositories(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + "..")
                .and(annotatedWith(CONTROLLER).or(annotatedWith(REST_CONTROLLER)))
                .should().dependOnClassesThat().areAnnotatedWith(REPOSITORY)
                .orShould().dependOnClassesThat().areAssignableTo(DATA_REPOSITORY)
                .because("controllers go through a service, which owns the transaction and the business rules")
                .allowEmptyShould(true);
    }

    /**
     * {@code @Transactional} (Spring or Jakarta) on classes or methods only inside {@code <rootPackage>.<servicePackage>}
     * and its sub-packages, e.g. {@code ("com.acme.shop", "service")}.
     */
    public static ArchRule transactionalOnlyIn(String rootPackage, String servicePackage) {
        String root = rootPackage + "..";
        String services = servicePackage.isEmpty() ? root : rootPackage + "." + servicePackage + "..";
        String reason = "transaction boundaries belong to the service layer";
        return CompositeArchRule
                .of(noClasses().that().resideInAPackage(root)
                        .and(annotatedWith(SPRING_TRANSACTIONAL).or(annotatedWith(JAKARTA_TRANSACTIONAL)))
                        .should().resideOutsideOfPackage(services).because(reason).allowEmptyShould(true))
                .and(noMethods().that().areDeclaredInClassesThat().resideInAPackage(root)
                        .and(annotatedWith(SPRING_TRANSACTIONAL)
                                .or(annotatedWith(JAKARTA_TRANSACTIONAL)))
                        .should().beDeclaredInClassesThat().resideOutsideOfPackage(services).because(reason)
                        .allowEmptyShould(true));
    }
}

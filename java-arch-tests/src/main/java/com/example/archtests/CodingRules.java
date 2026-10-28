// From github.com/barbulescu/architectural-tests, rule group "coding". Copied into your project: edit freely.
package com.example.archtests;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION;
import static com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JODATIME;

/**
 * Rules that are tedious to police in code review because they are about absence:
 * the reviewer has to notice something that should not be there. The conditions come ready-made with ArchUnit.
 *
 * <p>Every rule applies to {@code rootPackage} and its sub-packages only, e.g. {@code "com.acme.shop"}.
 */
public final class CodingRules {

    private CodingRules() {
    }

    /** No {@code System.out}, {@code System.err} or {@code printStackTrace()}: use a logger. */
    public static ArchRule noStandardStreams(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + "..").should(ACCESS_STANDARD_STREAMS);
    }

    public static ArchRule noJavaUtilLogging(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + "..").should(USE_JAVA_UTIL_LOGGING);
    }

    /** No {@code throw new Exception/RuntimeException/Throwable}: throw something a caller can handle specifically. */
    public static ArchRule noGenericExceptions(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + "..").should(THROW_GENERIC_EXCEPTIONS);
    }

    /** {@code java.time} replaced Joda-Time. */
    public static ArchRule noJodaTime(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + "..").should(USE_JODATIME)
                .because("modern Java projects use the [java.time] API instead");
    }

    /** No {@code @Autowired}, {@code @Inject}, {@code @Value} or {@code @Resource} on fields: use constructors. */
    public static ArchRule noFieldInjection(String rootPackage) {
        return noFields().that().areDeclaredInClassesThat().resideInAPackage(rootPackage + "..")
                .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
                .because("field injection is considered harmful; use constructor injection or setter injection instead");
    }
}

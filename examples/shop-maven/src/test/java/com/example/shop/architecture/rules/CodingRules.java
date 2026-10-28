// From github.com/barbulescu/architectural-tests, rule group "coding". Copied into your project: edit freely.
package com.example.shop.architecture.rules;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

/**
 * Rules that are tedious to police in code review because they are about absence:
 * the reviewer has to notice something that should not be there. All come ready-made with ArchUnit.
 */
public final class CodingRules {

    private CodingRules() {
    }

    /** No {@code System.out}, {@code System.err} or {@code printStackTrace()}: use a logger. */
    public static ArchRule noStandardStreams() {
        return NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
    }

    public static ArchRule noJavaUtilLogging() {
        return NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
    }

    /** No {@code throw new Exception/RuntimeException/Throwable}: throw something a caller can handle specifically. */
    public static ArchRule noGenericExceptions() {
        return NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
    }

    /** {@code java.time} replaced Joda-Time. */
    public static ArchRule noJodaTime() {
        return NO_CLASSES_SHOULD_USE_JODATIME;
    }

    /** No {@code @Autowired}, {@code @Inject}, {@code @Value} or {@code @Resource} on fields: use constructors. */
    public static ArchRule noFieldInjection() {
        return NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
    }
}

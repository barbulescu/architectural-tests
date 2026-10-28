package com.example.shop.architecture;

import com.example.shop.architecture.rules.CodingRules;
import com.example.shop.architecture.rules.CycleRules;
import com.example.shop.architecture.rules.LayeredArchitectureRules;
import com.example.shop.architecture.rules.NamingRules;
import com.example.shop.architecture.rules.TestPresenceRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

/**
 * The architecture rules of this project. ArchUnit reads compiled bytecode, so the rules cover Java and Kotlin.
 * Rule sources are in {@code architecture/rules/}, copied from github.com/barbulescu/architectural-tests.
 *
 * <p>To tolerate existing violations of a rule, wrap it in {@code FreezingArchRule.freeze(...)} and record them once
 * with {@code -Darchunit.freeze.refreeze=true}: they go to {@code archunit_store/} (commit it), new ones still fail.
 */
class ArchitectureTest {

    private static final String ROOT_PACKAGE = "com.example.shop";

    private static final JavaClasses PRODUCTION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT_PACKAGE);

    private static final JavaClasses PRODUCTION_AND_TESTS = new ClassFileImporter().importPackages(ROOT_PACKAGE);

    @Test
    void layers_are_respected() {
        // freeze(): violations that already exist are recorded in archunit_store/ and tolerated;
        // any NEW violation fails the build. Fixed violations are removed from the store automatically.
        freeze(LayeredArchitectureRules.webApplicationDomainInfrastructure(
                ROOT_PACKAGE, "web", "application", "domain", "infrastructure"))
                .check(PRODUCTION);
    }

    @Test
    void no_package_cycles() {
        CycleRules.noCyclesBetweenTopLevelPackages(ROOT_PACKAGE).check(PRODUCTION);
    }

    @Test
    void no_printing_to_the_console() {
        CodingRules.noStandardStreams(ROOT_PACKAGE).check(PRODUCTION);
    }

    @Test
    void no_java_util_logging() {
        CodingRules.noJavaUtilLogging(ROOT_PACKAGE).check(PRODUCTION);
    }

    @Test
    void controllers_live_in_the_web_package() {
        NamingRules.classesWithSuffixResideIn(ROOT_PACKAGE, "Controller", "web").check(PRODUCTION);
    }

    @Test
    void every_use_case_has_a_test() {
        TestPresenceRules.classesHaveATestClass(ROOT_PACKAGE, "application", "UseCase", PRODUCTION_AND_TESTS)
                .check(PRODUCTION_AND_TESTS);
    }
}

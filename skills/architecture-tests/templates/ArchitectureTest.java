package {{ROOT_PACKAGE}}.architecture;

import {{ROOT_PACKAGE}}.architecture.rules.CodingRules;
import {{ROOT_PACKAGE}}.architecture.rules.CycleRules;
import {{ROOT_PACKAGE}}.architecture.rules.HexagonalArchitectureRules;
import {{ROOT_PACKAGE}}.architecture.rules.LayeredArchitectureRules;
import {{ROOT_PACKAGE}}.architecture.rules.NamingRules;
import {{ROOT_PACKAGE}}.architecture.rules.SpringRules;
import {{ROOT_PACKAGE}}.architecture.rules.TestPresenceRules;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * The architecture rules of this project. ArchUnit reads compiled bytecode, so the rules cover Java and Kotlin.
 * Rule sources are in {@code architecture/rules/}, copied from github.com/barbulescu/architectural-tests.
 *
 * <p>To tolerate existing violations of a rule, wrap it in {@code FreezingArchRule.freeze(...)} and record them once
 * with {@code -Darchunit.freeze.refreeze=true}: they go to {@code archunit_store/} (commit it), new ones still fail.
 */
class ArchitectureTest {

    /** Every rule checks this package and its sub-packages only; the other packages below are relative to it. */
    private static final String ROOT_PACKAGE = "{{ROOT_PACKAGE}}";

    private static final JavaClasses PRODUCTION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT_PACKAGE);

    // [test-presence] needs production AND test classes.
    private static final JavaClasses PRODUCTION_AND_TESTS = new ClassFileImporter().importPackages(ROOT_PACKAGE);

    // [layers]
    @Test
    void layers_are_respected() {
        LayeredArchitectureRules.webApplicationDomainInfrastructure(
                ROOT_PACKAGE, "{{WEB_PACKAGE}}", "{{APPLICATION_PACKAGE}}", "{{DOMAIN_PACKAGE}}", "{{INFRASTRUCTURE_PACKAGE}}")
                .check(PRODUCTION);
    }

    // [hexagonal]
    @Test
    void hexagonal_architecture_is_respected() {
        HexagonalArchitectureRules.hexagonal(
                ROOT_PACKAGE, "{{DOMAIN_MODEL_PACKAGE}}", "{{DOMAIN_SERVICE_PACKAGE}}", "{{APPLICATION_PACKAGE}}",
                Map.of("{{ADAPTER_NAME}}", "{{ADAPTER_PACKAGE}}"))
                .check(PRODUCTION);
    }

    // [cycles]
    @Test
    void no_package_cycles() {
        CycleRules.noCyclesBetweenTopLevelPackages(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [coding] one method per selected CodingRules method
    @Test
    void no_printing_to_the_console() {
        CodingRules.noStandardStreams(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_java_util_logging() {
        CodingRules.noJavaUtilLogging(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_generic_exceptions() {
        CodingRules.noGenericExceptions(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_joda_time() {
        CodingRules.noJodaTime(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_field_injection() {
        CodingRules.noFieldInjection(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [naming] one method per convention
    @Test
    void controllers_live_in_the_web_package() {
        NamingRules.classesWithSuffixResideIn(ROOT_PACKAGE, "{{SUFFIX}}", "{{PACKAGE}}").check(PRODUCTION);
    }

    // [test-presence]
    @Test
    void every_use_case_has_a_test() {
        TestPresenceRules.classesHaveATestClass(ROOT_PACKAGE, "{{PACKAGE}}", "{{SUFFIX}}", PRODUCTION_AND_TESTS)
                .check(PRODUCTION_AND_TESTS);
    }

    // [spring]
    @Test
    void controllers_do_not_access_repositories() {
        SpringRules.controllersDoNotAccessRepositories(ROOT_PACKAGE).check(PRODUCTION);
    }

    // [spring]
    @Test
    void transactions_only_in_services() {
        SpringRules.transactionalOnlyIn(ROOT_PACKAGE, "{{SERVICE_PACKAGE}}").check(PRODUCTION);
    }
}

package {{BASE_PACKAGE}}.architecture;

import {{BASE_PACKAGE}}.architecture.rules.CodingRules;
import {{BASE_PACKAGE}}.architecture.rules.CycleRules;
import {{BASE_PACKAGE}}.architecture.rules.HexagonalArchitectureRules;
import {{BASE_PACKAGE}}.architecture.rules.LayeredArchitectureRules;
import {{BASE_PACKAGE}}.architecture.rules.NamingRules;
import {{BASE_PACKAGE}}.architecture.rules.SpringRules;
import {{BASE_PACKAGE}}.architecture.rules.TestPresenceRules;
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

    private static final String BASE_PACKAGE = "{{BASE_PACKAGE}}";

    private static final JavaClasses PRODUCTION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

    // [test-presence] needs production AND test classes.
    private static final JavaClasses PRODUCTION_AND_TESTS = new ClassFileImporter().importPackages(BASE_PACKAGE);

    // [layers]
    @Test
    void layers_are_respected() {
        LayeredArchitectureRules.webApplicationDomainInfrastructure(
                "{{WEB_PACKAGE}}", "{{APPLICATION_PACKAGE}}", "{{DOMAIN_PACKAGE}}", "{{INFRASTRUCTURE_PACKAGE}}")
                .check(PRODUCTION);
    }

    // [hexagonal]
    @Test
    void hexagonal_architecture_is_respected() {
        HexagonalArchitectureRules.hexagonal(
                "{{DOMAIN_MODEL_PACKAGE}}", "{{DOMAIN_SERVICE_PACKAGE}}", "{{APPLICATION_PACKAGE}}",
                Map.of("{{ADAPTER_NAME}}", "{{ADAPTER_PACKAGE}}"))
                .check(PRODUCTION);
    }

    // [cycles]
    @Test
    void no_package_cycles() {
        CycleRules.noCyclesBetweenTopLevelPackages(BASE_PACKAGE).check(PRODUCTION);
    }

    // [coding] one method per selected CodingRules method
    @Test
    void no_printing_to_the_console() {
        CodingRules.noStandardStreams().check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_java_util_logging() {
        CodingRules.noJavaUtilLogging().check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_generic_exceptions() {
        CodingRules.noGenericExceptions().check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_joda_time() {
        CodingRules.noJodaTime().check(PRODUCTION);
    }

    // [coding]
    @Test
    void no_field_injection() {
        CodingRules.noFieldInjection().check(PRODUCTION);
    }

    // [naming] one method per convention
    @Test
    void controllers_live_in_the_web_package() {
        NamingRules.classesWithSuffixResideIn("{{SUFFIX}}", "{{PACKAGE}}").check(PRODUCTION);
    }

    // [test-presence]
    @Test
    void every_use_case_has_a_test() {
        TestPresenceRules.classesHaveATestClass("{{PACKAGE}}", "{{SUFFIX}}", PRODUCTION_AND_TESTS)
                .check(PRODUCTION_AND_TESTS);
    }

    // [spring]
    @Test
    void controllers_do_not_access_repositories() {
        SpringRules.controllersDoNotAccessRepositories().check(PRODUCTION);
    }

    // [spring]
    @Test
    void transactions_only_in_services() {
        SpringRules.transactionalOnlyIn("{{SERVICE_PACKAGE}}").check(PRODUCTION);
    }
}

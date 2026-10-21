package com.example.shop.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

/**
 * ArchUnit reads compiled bytecode, so this one rule covers the Java and the Kotlin code.
 * It is a plain JUnit test: no plugin, no extra build step, runs wherever {@code ./mvnw test} runs.
 */
class LayerRulesTest {

    private static final JavaClasses PRODUCTION_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.shop");

    static final ArchRule LAYERS = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Web").definedBy("..web..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")

            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Web")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .because("the web layer talks to use cases, and only the composition root wires in infrastructure");

    @Test
    void layers_are_respected() {
        // freeze(): violations that already exist are recorded in archunit_store/ and tolerated;
        // any NEW violation fails the build. Fixed violations are removed from the store automatically.
        freeze(LAYERS).check(PRODUCTION_CLASSES);
    }
}

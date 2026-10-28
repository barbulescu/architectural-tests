// From github.com/barbulescu/architectural-tests, rule group "layers". Copied into your project: edit freely.
package com.example.shop.architecture.rules;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Classic layers: web -> application -> domain, with infrastructure as adapters that only the composition root
 * (a class outside every layer, e.g. the main class) wires in. Infrastructure depends on the domain (it implements
 * the domain's ports); nothing depends on infrastructure.
 *
 * <p>Changing a rule's text resets a frozen baseline ({@code archunit_store/}), so keep the texts stable.
 */
public final class LayeredArchitectureRules {

    private LayeredArchitectureRules() {
    }

    /** Package patterns use ArchUnit syntax, e.g. {@code "..web.."} or {@code "com.acme.shop.web.."}. */
    public static ArchRule webApplicationDomainInfrastructure(
            String webPackage, String applicationPackage, String domainPackage, String infrastructurePackage) {
        return layers()
                .layer("Web").definedBy(webPackage)
                .layer("Application").definedBy(applicationPackage)
                .layer("Domain").definedBy(domainPackage)
                .layer("Infrastructure").definedBy(infrastructurePackage)

                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Web")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .because("the web layer talks to use cases, and only the composition root wires in infrastructure");
    }

    /**
     * Starting point for any other layering: add {@code .layer(...)} and {@code .whereLayer(...)} calls.
     * Dependencies on classes outside every layer (the JDK, libraries, the composition root) are ignored.
     */
    public static LayeredArchitecture layers() {
        return layeredArchitecture().consideringOnlyDependenciesInLayers();
    }
}

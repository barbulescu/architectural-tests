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

    /**
     * Layer packages are relative to {@code rootPackage}: {@code ("com.acme.shop", "web", ...)} means
     * {@code com.acme.shop.web} and its sub-packages. Only classes below {@code rootPackage} are checked.
     */
    public static ArchRule webApplicationDomainInfrastructure(
            String rootPackage, String webPackage, String applicationPackage, String domainPackage,
            String infrastructurePackage) {
        return layers()
                .layer("Web").definedBy(inside(rootPackage, webPackage))
                .layer("Application").definedBy(inside(rootPackage, applicationPackage))
                .layer("Domain").definedBy(inside(rootPackage, domainPackage))
                .layer("Infrastructure").definedBy(inside(rootPackage, infrastructurePackage))

                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Web")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Web")
                .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
                .because("the web layer talks to use cases, and only the composition root wires in infrastructure");
    }

    /**
     * Starting point for any other layering: add {@code .layer(...).definedBy("com.acme.shop.x..")} and
     * {@code .whereLayer(...)} calls. Dependencies on classes outside every layer (the JDK, libraries, the
     * composition root) are ignored, so defining the layers below the root package limits the rule to it.
     */
    public static LayeredArchitecture layers() {
        return layeredArchitecture().consideringOnlyDependenciesInLayers();
    }

    private static String inside(String rootPackage, String subPackage) {
        return subPackage.isEmpty() ? rootPackage + ".." : rootPackage + "." + subPackage + "..";
    }
}
